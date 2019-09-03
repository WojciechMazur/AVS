package pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.protocol

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import com.softwaremill.quicklens._
import pl.edu.agh.wmazur.avs.model.entity.intersection.IntersectionManager.Protocol.IntersectionCrossingRequest
import pl.edu.agh.wmazur.avs.model.entity.intersection.reservation.ReservationArray.Timestamp
import pl.edu.agh.wmazur.avs.model.entity.intersection.workers.IntersectionCoordinator
import pl.edu.agh.wmazur.avs.model.entity.intersection.{
  AutonomousIntersectionManager,
  IntersectionManager
}
import pl.edu.agh.wmazur.avs.model.entity.road.{Lane, Road}
import pl.edu.agh.wmazur.avs.model.entity.utils.MathUtils.DoubleUtils
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.Velocity
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousVehicleDriver.ExtendedProtocol
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.VehicleDriver.Protocol.{
  ReservationConfirmed,
  ReservationDetails,
  ReservationRejected
}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.{
  AutonomousVehicleDriver,
  ReservationChecker,
  VehicleArrivalEstimator,
  VehiclePilot
}
import pl.edu.agh.wmazur.avs.simulation.TickSource

import scala.collection.mutable
import scala.concurrent.duration._
import scala.util.{Failure, Success}

trait PreperingReservation {
  self: AutonomousVehicleDriver with Driving with VehiclePilot =>
  import AutonomousVehicleDriver._
  import PreperingReservation._

  type ArrivalToDeperatureLanesVelocities =
    mutable.Map[Lane#Id, Map[Lane, Velocity]]
  type IntersectionToLaneVelocities =
    mutable.Map[ActorRef[AutonomousIntersectionManager.Protocol],
                ArrivalToDeperatureLanesVelocities]

  var lastRequestTimestamp: Option[Timestamp] = None
  var nextReservationRequestAttempt: Option[Timestamp] = None

  var lastReservationRequest
    : Option[IntersectionManager.Protocol.IntersectionCrossingRequest] = None
  var isAwaitingForAcceptance: Boolean = false
  var reservationDetails: Option[ReservationDetails] = None

  var rejectionCounter: Int = 0

  val cachedMaxVelocities: IntersectionToLaneVelocities = mutable.Map.empty

  val preperingReservation: Behavior[AutonomousVehicleDriver.Protocol] =
    Behaviors.receiveMessagePartial {
      case AskForMaximalCrossingVelocities
          if nextIntersectionManager
            .flatMap(cachedMaxVelocities.get)
            .map(_.get(currentLane.id))
            .isDefined =>
        println("Try send reservation request")
        context.self ! TrySendReservationRequest
        Behaviors.same

      case AskForMaximalCrossingVelocities if destination.isDefined =>
        if (nextIntersectionManager.nonEmpty) {
          nextIntersectionManager.get ! IntersectionCoordinator.Protocol
            .GetMaxCrossingVelocity(context.self,
                                    currentLane.id,
                                    destination.get,
                                    vehicle.spec)
        }
        Behaviors.same

      case AskForMaximalCrossingVelocities =>
        context.log.info(
          s"Not ready yet destination: ${destination}, intersectio manager ${nextIntersectionManager
            .map(_.path.name)}")
        //not ready yet
        Behaviors.same

      case MaxCrossingVelocities(intersectionRef,
                                 arrivalLaneId,
                                 maxVelocities) =>
        if (nextIntersectionManager.contains(intersectionRef) &&
            currentLane.id == arrivalLaneId) {

          val arrivalLaneVelocities =
            cachedMaxVelocities.getOrElseUpdate(intersectionRef,
                                                mutable.Map.empty)
          arrivalLaneVelocities.update(arrivalLaneId, maxVelocities)

          if (maxVelocities.values.exists(_.isZero)) {
            context.self ! AskForMaximalCrossingVelocities
          } else if (hasPlannedPath) {
            context.self ! TrySendReservationRequest
          } else {
            sys.error("Unknown behaviour")
          }
        }
        Behaviors.same

      case TrySendReservationRequest
          if currentPath.isEmpty ||
            nextIntersectionManager.isEmpty =>
        nextReservationRequestAttempt =
          Some(currentTime + TickSource.timeStep.toMillis * 5)
        context.log.info(s"Retry at $nextReservationRequestAttempt")
        Behaviors.same

      case TrySendReservationRequest =>
        nextIntersectionManager
          .flatMap(cachedMaxVelocities.get)
          .flatMap(_.get(currentLane.id)) match {
          case None => sys.error("This should not happend!")
          case Some(maxVelocities) =>
            context.log.info("Creating reservation request")
            withVehicle {
              stopBeforeIntersectionSchedule(currentTime) match {
                case optSchedule @ Some(_) =>
                  vehicle.withAccelerationSchedule(optSchedule)
                case None => followCurrentLane().vehicle.stop
              }
            }

            val estimations = maxVelocities
              .filterKeys(_.id == nextLane.id)
              .mapValues(estimateArrival)
              .collect {
                case (lane, Some(estimation)) => lane -> estimation
              }

            val proposals = estimations.map {
              case (departureLane,
                    VehicleArrivalEstimator.Result(arrivalTime,
                                                   arrivalVelocity,
                                                   accScheduleProposal)) =>
                IntersectionCrossingRequest.Proposal(
                  currentLane.id,
                  departureLane,
                  arrivalTime,
                  arrivalVelocity,
                  maxVelocities(departureLane),
                  accScheduleProposal)
            }

            val request = IntersectionCrossingRequest(
              context.self,
              IntersectionCrossingRequest.CrossingVehicleSpec(vehicle.spec),
              proposals.toList,
              currentTime,
              context.self
            )
            context.log.info("Awaiting for response")

            isAwaitingForAcceptance = true
            lastReservationRequest = Some(request)
            lastRequestTimestamp = Some(currentTime)
            nextIntersectionManager.get ! request
        }
        Behaviors.same

      case ReservationRequestTimeout =>
        context.log.error("Reservation request timeout")
        withVehicle(vehicle.withAccelerationSchedule(None))
        isAwaitingForAcceptance = false
        isRetryingToMakeReservation = true

        context.self ! TrySendReservationRequest
        Behaviors.same

      case ReservationRejected(requestId,
                               nextAllowedCommunicationTimestamp,
                               reason) =>
        context.log.info(s"Reservation $requestId rejected, $reason")
        if (isRetryingToMakeReservation) {
//          println(s"${vehicle.id} rejected by intersection")
          rejectionCounter += 1
        } else {
          rejectionCounter = 1
        }
        withVehicle(vehicle.withAccelerationSchedule(None))
        lastRequestTimestamp = None
        isAwaitingForAcceptance = false
        isRetryingToMakeReservation = true
        if (lastReservationRequest.map(_.id).contains(requestId)) {
          val delay =
            (nextAllowedCommunicationTimestamp - currentTime).millis
              .max(sendingRequestDelay)
          nextReservationRequestAttempt = Some(currentTime + delay.toMillis)
        }
        Behaviors.same

      case ReservationConfirmed(reservationId, requestId, details) =>
        context.log.info("Reservation confirmed")

        lastRequestTimestamp = None
        isAwaitingForAcceptance = false
        val updatedGauges = updateGauges

        ReservationChecker
          .check(
            time = currentTime,
            timeEnd = details.arrivalTime,
            velocity = vehicle.velocity,
            velocityEnd = details.arrivalVelocity,
            velocityMax =
              VehiclePilot.calcMaxAllowedVelocity(vehicle, currentLane),
            distanceTotal =
              updatedGauges.driverGauges.distanceToNextIntersection.get,
            acceleration = vehicle.spec.maxAcceleration,
            deceleration = vehicle.spec.maxDeceleration
          )
          .map { schedule =>
            assert(schedule.timestamps.last.timeEnd == details.arrivalTime)
            isMaintaingReservation = true
            nextSchedulerCheckTimestamp = Some(currentTime + 250L)
            if (isRetryingToMakeReservation) {
              context.log.info(
                s"Reservation accepted after $rejectionCounter retries")
            }
            isRetryingToMakeReservation = false
            nextReservationRequestAttempt = None
            reservationDetails = Some(details)
            println(s"""
                |Arrival velocity: ${details.arrivalVelocity}
                |Exit velocity:    ${details.accelerationProfile
                         .toAccelerationSchedule(details.arrivalTime)
                         .calcFinalVelocity(details.arrivalVelocity)}
                |Arrival Time:     ${details.arrivalTime}
                |""".stripMargin)
            context.log.debug(
              s"Reservation accepted - currentTime: $currentTime arrivalTime: ${details.arrivalTime}, velocity: ${details.arrivalVelocity}")
            withVehicle(vehicle.withAccelerationSchedule(Some(schedule)))
          }
          .recover {
            case err =>
              //              err.printStackTrace()
              context.log.error("Reservation check failed: {}", err.getMessage)
              rejectionCounter += 1
              details.intersectionManagerRef ! IntersectionManager.Protocol
                .CancelReservation(reservationId, context.self)
              nextReservationRequestAttempt =
                Some(currentTime + sendingRequestDelay.toMillis)
              isRetryingToMakeReservation = true
              reservationDetails = None
              context.self ! TrySendReservationRequest
              withVehicle(vehicle.withAccelerationSchedule(None))
          }
          .get

        Behaviors.same

    }

  def estimateArrival(
      maxArrivalVelocity: Velocity): Option[VehicleArrivalEstimator.Result] = {
    val maxVelocity = VehiclePilot.calcMaxAllowedVelocity(vehicle, currentLane)
    val initialParams = VehicleArrivalEstimator.Parameters(
      initialTime = currentTime,
      velocity = vehicle.velocity,
      distanceTotal = driverGauges.distanceToNextIntersection.get,
      maxVelocity = maxVelocity,
      finalVelocity = Math.min(maxVelocity, maxArrivalVelocity),
      maxAcceleration = vehicle.spec.maxAcceleration,
      maxDeceleration = vehicle.spec.maxDeceleration
    )

    val estimationParams = vehicle.accelerationSchedule match {
      case Some(_) =>
        buildEstimationParamsWithAcclerationSchedule(initialParams)
      case None => buildEstimationParamsNoAcclerationSchedule(initialParams)
    }

    VehicleArrivalEstimator.estimate(estimationParams) match {
      case Success(result)
          if VehicleArrivalEstimator.isValid(estimationParams, result) =>
        Some(result)
      case Success(_) =>
        context.log.error("Estimation failed validity checks")
        None
      case Failure(exception) =>
        exception.printStackTrace()
        context.log
          .error("Failed to estimate arrival: {}", exception.getMessage)
        None
    }
  }

  def hasArrivedInTime: Boolean = {
    reservationDetails.exists { reservationParams =>
      val a1 = currentTime - TickSource.timeStep.toMillis
      val a2 = currentTime
      val b1 = reservationParams.arrivalTime - reservationParams.safetyBufferBefore.toMillis
      val b2 = reservationParams.arrivalTime + reservationParams.safetyBufferAfter.toMillis

      val valid1 = a1 < b1 && b1 <= a2
      val valid2 = a1 < b2 && b2 <= a2
      val valid3 = b1 <= a1 && a2 < b2

      val result = List(valid1, valid2, valid3).contains(true)
      if (!result) {
        context.log.error(
          s"arrival time check failed, earliest $b1, current: $currentTime, lastest: $b2")
      }
      result
    }
  }

  def hasArrivedWithExpectedVelocity: Boolean = {
    reservationDetails.exists { reservationParams =>
      val v1 = reservationParams.arrivalVelocity
      val v2 = vehicle.velocity

      val result = (v1 - v2).abs <= arrivalVelocityThreshold
      if (!result) {
        System.err.println(s"Expected velocity $v1, currentVelocity $v2")
      }
      result
    }
  }

  private def buildEstimationParamsWithAcclerationSchedule(
      params: VehicleArrivalEstimator.Parameters)
    : VehicleArrivalEstimator.Parameters = {
    val accelerationSchedule = this.vehicle.accelerationSchedule.get
    val timeAtExpectedReplyTime = params.initialTime + maxExpectedIntersectionManagerReplyTime.toMillis

    val (distanceTraveled, finalVelocity) =
      accelerationSchedule.calculateFinalStateAtTime(
        initialTime = params.initialTime,
        initialVelocity = params.velocity,
        finalTime = timeAtExpectedReplyTime)

    val distanceToNextIntersection = driverGauges.distanceToNextIntersection.get
    if (distanceTraveled.asMeters > distanceToNextIntersection.asMeters) {
      sys.error(
        "Vehicle should not be able to reach intersection before intersection manager reply")
    }

    params
      .modify(_.initialTime)
      .setTo(timeAtExpectedReplyTime)
      .modify(_.velocity)
      .setTo(finalVelocity)
      .modify(_.velocity)
      .setToIf(finalVelocity.isZero)(0.0)
      .modify(_.distanceTotal)
      .using(_ - distanceTraveled)
  }

  private def buildEstimationParamsNoAcclerationSchedule(
      params: VehicleArrivalEstimator.Parameters)
    : VehicleArrivalEstimator.Parameters = {
    if (params.velocity.isZero) {
      params
        .modify(_.maxAcceleration)
        .using(acc => (acc - arrivalEstimationAccelerationReduction).max(0))
        .modify(_.maxDeceleration)
        .using(dec => (dec + arrivalEstimationAccelerationReduction).min(0))
    } else {
      params
        .modify(_.initialTime)
        .using(_ + maxExpectedIntersectionManagerReplyTime.toMillis)
    }
  }
}

object PreperingReservation {
  final val maxExpectedIntersectionManagerReplyTime: FiniteDuration =
    TickSource.timeStep
  final val arrivalEstimationAccelerationReduction = 1.0
  final val sendingRequestDelay = 5 * TickSource.timeStep
  final val reservationRequestTimeout = 250.millis
  final val arrivalVelocityThreshold = 3.0

  trait Protocol {
    case object TrySendReservationRequest extends ExtendedProtocol
    case object ReservationRequestTimeout extends ExtendedProtocol

    case class MaxCrossingVelocities(
        intersectionRef: ActorRef[IntersectionManager.Protocol],
        arrivalLaneId: Lane#Id,
        allowedVelocities: Map[Lane, Velocity])
        extends ExtendedProtocol
    case class NoPathForLanes(laneId: Lane#Id, roadId: Road#Id)
        extends ExtendedProtocol
  }

}
