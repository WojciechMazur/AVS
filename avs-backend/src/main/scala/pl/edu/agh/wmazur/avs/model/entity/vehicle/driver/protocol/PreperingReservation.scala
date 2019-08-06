package pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.protocol

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import com.softwaremill.quicklens._
import pl.edu.agh.wmazur.avs.model.entity.intersection.IntersectionManager.Protocol.IntersectionCrossingRequest
import pl.edu.agh.wmazur.avs.model.entity.intersection.workers.IntersectionCoordinator
import pl.edu.agh.wmazur.avs.model.entity.intersection.{
  Intersection,
  IntersectionManager
}
import pl.edu.agh.wmazur.avs.model.entity.road.{Lane, Road}
import pl.edu.agh.wmazur.avs.model.entity.utils.MathUtils.DoubleUtils
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleArrivalEstimator
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.Velocity
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousVehicleDriver.ExtendedProtocol
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.{
  AutonomousVehicleDriver,
  VehiclePilot
}
import pl.edu.agh.wmazur.avs.simulation.TickSource

import scala.collection.mutable
import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success}

trait PreperingReservation {
  self: AutonomousVehicleDriver with Driving =>
  import AutonomousVehicleDriver._

  final val maxExpectedIntersectionManagerReplyTime
    : FiniteDuration = 2 * TickSource.timeStep
  final val arrivalEstimationAccelerationReduction = 1.0

  val reservation: Behavior[Protocol] = Behaviors.receiveMessagePartial {
    case AskForMaximalCrossingVelocities =>
      if (nextIntersectionManager.nonEmpty) {

        nextIntersectionManager.get ! IntersectionCoordinator.Protocol
          .GetMaxCrossingVelocity(context.self,
                                  currentLane.id,
                                  destination.get.id,
                                  vehicle.spec)
      }
      Behaviors.same
    case MaxCrossingVelocities(intersectionRef, arrivalLaneId, maxVelocities) =>
      if (nextIntersectionManager.contains(intersectionRef) &&
          currentLane.id == arrivalLaneId) {

        val estimations = maxVelocities
          .mapValues(estimateArrival)
          .collect {
            case (lane, Some(estimation)) => lane -> estimation
          }

        val proposals = estimations.map {
          case (departureLane,
                VehicleArrivalEstimator.Result(arrivalTime,
                                               arrivalVelocity,
                                               accScheduleProposal)) =>
            IntersectionCrossingRequest.Proposal(currentLane.id,
                                                 departureLane.id,
                                                 arrivalTime,
                                                 arrivalVelocity,
                                                 maxVelocities(departureLane),
                                                 accScheduleProposal)
        }
        val request = IntersectionCrossingRequest(
          vehicle.id,
          IntersectionCrossingRequest.CrossingVehicleSpec(vehicle.spec),
          proposals.toList,
          currentTime,
          context.self
        )

        intersectionRef ! request
      }

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

    val estimationParams = accelerationSchedule match {
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
        context.log
          .error("Failed to estimate arrival: {}", exception.getMessage)
        None
    }
  }

  private def buildEstimationParamsWithAcclerationSchedule(
      params: VehicleArrivalEstimator.Parameters)
    : VehicleArrivalEstimator.Parameters = {
    val accelerationSchedule = this.accelerationSchedule.get
    val timeAtExpectedReplyTime = params.initialTime + maxExpectedIntersectionManagerReplyTime.toMillis

    val (distanceTraveled, finalVelocity) =
      accelerationSchedule.calculateFinalState(
        initialTime = params.initialTime,
        initialVelocity = params.velocity,
        finalTime = timeAtExpectedReplyTime)

    val distanceToNextIntersection = driverGauges.distanceToNextIntersection.get
    if (distanceTraveled.meters > distanceToNextIntersection.meters) {
      sys.error(
        "Vehicle should not be able to reach intersection before intersection manager reply")
    }

    params
      .modify(_.initialTime)
      .using(_ + timeAtExpectedReplyTime)
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
        .using(acc => (acc - arrivalEstimationAccelerationReduction).min(0))
        .modify(_.maxDeceleration)
        .using(dec => (dec + arrivalEstimationAccelerationReduction).max(0))
    } else {
      params
        .modify(_.initialTime)
        .using(_ + maxExpectedIntersectionManagerReplyTime.toMillis)
    }
  }

}

object PreperingReservation {

  trait Protocol {
    case class MaxCrossingVelocities(
        intersectionRef: ActorRef[IntersectionManager.Protocol],
        arrivalLaneId: Lane#Id,
        allowedVelocities: Map[Lane, Velocity])
        extends ExtendedProtocol
    case class NoPathForLanes(laneId: Lane#Id, roadId: Road#Id)
        extends ExtendedProtocol
  }
}
