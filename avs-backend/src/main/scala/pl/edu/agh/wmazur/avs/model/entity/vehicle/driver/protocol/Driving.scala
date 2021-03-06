package pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.protocol

import java.util.concurrent.TimeUnit

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import pl.edu.agh.wmazur.avs.model.entity.intersection.IntersectionManager
import pl.edu.agh.wmazur.avs.model.entity.intersection.reservation.ReservationArray.Timestamp
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousVehicleDriver.ExtendedProtocol
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.{
  AutonomousVehicleDriver,
  VehiclePilot
}
import pl.edu.agh.wmazur.avs.simulation.TickSource.TickDelta
import pl.edu.agh.wmazur.avs.simulation.stage.DriversMovementStage

trait Driving extends VehiclePilot {
  self: AutonomousVehicleDriver with PreperingReservation =>

  import AutonomousVehicleDriver._
  var isTraversing = false
  var isMaintaingReservation = false
  var isRetryingToMakeReservation = false
  var hasLeavedAdmissionControlZone = true

  var nextSchedulerCheckTimestamp: Option[Timestamp] = None

  private def withoutChange: self.type = this

  def move(replyTo: ActorRef[DriversMovementStage.Protocol],
           tickDelta: TickDelta)(
      movementDecidingFn: () => self.type): Behavior[Protocol] = {
    val oldPosition = vehicle.position
    val newState = prepareToMove().withVehicle {
      movementDecidingFn().vehicle
        .move(currentTime, tickDelta.toUnit(TimeUnit.SECONDS))
    }

    this.vehicle = newState.vehicle
    val newPosition = vehicle.position
    replyTo ! DriversMovementStage.DriverMoved(context.self,
                                               oldPosition,
                                               newPosition)
    Behaviors.same
  }

  def controlScheduler(): self.type = {
    if (hasClearLaneToIntersection) {
      withoutChange
    } else {
      val stoppingDistance = VehiclePilot.calcStoppingDistance(
        vehicle.velocity,
        vehicle.spec.maxDeceleration)
      val followingDistance = stoppingDistance + VehiclePilot.minimumDistanceBetweenCars
      if (driverGauges.distanceToNextIntersection.exists(_ > followingDistance)) {
        withoutChange
      } else {
        withVehicle(vehicle.withAccelerationSchedule(None))
          .followCurrentLane()
          .applyBasicThrothelling()
      }
    }
  }

  def slowDownForReservationRetry(): self.type = {
    val currentVelocity = vehicle.velocity
    val stoppingDistance = VehiclePilot.calcStoppingDistance(
      currentVelocity,
      vehicle.spec.maxDeceleration)

    if (driverGauges.distanceToCollisionWithCarInFront.forall(
          _ > stoppingDistance)) {
      withVehicle {
        vehicle
          .withTargetVelocity((currentVelocity * 0.99).max(
            VehiclePilot.calcMaxAllowedVelocity(vehicle, currentLane) - 5))
          .withAcceleration(vehicle.spec.maxDeceleration)
      }.followCurrentLane()
        .stopBeforeVehicleInFront()
        .stopBeforeIntersection()
    } else {
      withVehicle {
        vehicle.stop

      }
    }
  }

  def traverseIntersection(tickDelta: TickDelta)(): self.type = {
    if (driverGauges.isWithinIntersection) {
      val reservationDetails = this.reservationDetails.get
      val (_, maxCrossingVelociy) =
        cachedMaxVelocities(reservationDetails.intersectionManagerRef)(
          reservationDetails.arrivalLaneId)
          .find(_._1.id == reservationDetails.departureLaneId)
          .get

      val (updatedDetails, updatedSelf) =
        takeSteeringActiorsForTraversing(reservationDetails)
          .followAccelerationProfile(reservationDetails,
                                     tickDelta,
                                     maxCrossingVelociy)
      this.reservationDetails = Some(updatedDetails)
      updatedSelf.asInstanceOf[self.type]
    } else {
      reservationDetails.get.intersectionManagerRef ! IntersectionManager.Protocol
        .ReservationCompleted(context.self,
                              reservationDetails.get.reservationId)
      prevIntersectionManager = nextIntersectionManager
      previousIntersectionPosition = nextIntersectionPosition
      prevIntersectionGeometry = nextIntersectionGeometry
      nextIntersectionManager = None
      nextIntersectionPosition = None
      nextIntersectionGeometry = None
      isTraversing = false
      withoutChange
    }
  }

  lazy val drive: Behavior[Protocol] = Behaviors.receiveMessagePartial {

    case Move(replyTo, tickDelta) if isAwaitingForAcceptance =>
      move(replyTo, tickDelta) {
        controlScheduler
      }
    case msg @ Move(_, _)
        if isMaintaingReservation &&
          driverGauges.isWithinIntersection =>
//      assert(hasArrivedInTime)
//      assert(hasArrivedWithExpectedVelocity)
      context.self ! msg
      isMaintaingReservation = false
      isTraversing = true
      hasLeavedAdmissionControlZone = false
      nextSchedulerCheckTimestamp = None
      context.log.debug("Starting traversing")
      Behaviors.same

    case msg if isMaintaingReservation && !hasClearLaneToIntersection =>
      context.log.warning(
        s"Canceling reservation ${reservationDetails.get.reservationId}. To close to preceding vehicle ${driverGauges.distanceToCollisionWithCarInFront
          .map(_.asMeters)}")
      context.self ! msg
      cancelReservation
      Behaviors.same

//    case msg
//        if isMaintaingReservation && estimateArrival(
//          reservationDetails.get.arrivalVelocity).exists(
//          _.arrivalTime > reservationDetails.get.arrivalTime + ReservationSystem.lateArrivalThreshold.toMillis) =>
//      context.self ! msg
//      cancelReservation

    case Move(replyTo, tickDelta) if isMaintaingReservation =>
      move(replyTo, tickDelta) { () =>
        followCurrentLane().stopBeforeIntersection().stopBeforeVehicleInFront()
      }

    case Move(replyTo, tickDelta) if isTraversing =>
      move(replyTo, tickDelta) {
        traverseIntersection(tickDelta)
      }
    case Move(replyTo, tickDelta) if isRetryingToMakeReservation =>
      move(replyTo, tickDelta) {
        slowDownForReservationRetry
      }
      Behaviors.same

    case Move(replyTo, tickDelta) =>
      move(replyTo, tickDelta) {
        followCurrentLane().applyBasicThrothelling
      }
  }

  def cancelReservation: Behavior[Protocol] = {
    nextIntersectionManager.get ! IntersectionManager.Protocol
      .CancelReservation(reservationDetails.get.reservationId, context.self)
    nextReservationRequestAttempt = Some(
      currentTime + PreperingReservation.sendingRequestDelay.toMillis)
    reservationDetails = None
    withVehicle(vehicle.withAccelerationSchedule(None))
    isMaintaingReservation = false
    Behaviors.same
  }
}

object Driving {
  trait Protocol {
    case class Move(replyTo: ActorRef[DriversMovementStage.Protocol],
                    tickDelta: TickDelta)
        extends ExtendedProtocol
  }
}
