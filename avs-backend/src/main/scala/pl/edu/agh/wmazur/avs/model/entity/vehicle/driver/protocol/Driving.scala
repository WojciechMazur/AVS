package pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.protocol

import java.util.concurrent.TimeUnit

import com.softwaremill.quicklens._
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import pl.edu.agh.wmazur.avs.model.entity.intersection.IntersectionManager
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousVehicleDriver.ExtendedProtocol
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.{
  AutonomousVehicleDriver,
  VehiclePilot
}
import pl.edu.agh.wmazur.avs.simulation.TickSource
import pl.edu.agh.wmazur.avs.simulation.TickSource.TickDelta
import pl.edu.agh.wmazur.avs.simulation.stage.DriversMovementStage

trait Driving extends VehiclePilot {
  self: AutonomousVehicleDriver with PreperingReservation =>

  import AutonomousVehicleDriver._
  var isTraversing = false
  var isMaintaingReservation = false

  private def withoutChange: self.type = this

  def move(replyTo: ActorRef[DriversMovementStage.Protocol],
           tickDelta: TickDelta)(
      movementDecidingFn: () => self.type): Behavior[Protocol] = {
    val oldPosition = vehicle.position

    val newPosition = prepareToMove()
      .withVehicle {
        movementDecidingFn().vehicle
          .move(currentTime, tickDelta.toUnit(TimeUnit.SECONDS))
      }
      .vehicle
      .position

    replyTo ! DriversMovementStage.DriverMoved(context.self,
                                               oldPosition,
                                               newPosition)
    Behaviors.same
  }

  def controlScheduler(): self.type = {
    if (hasClearLnaeToIntersection) {
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
      assert(hasArrivedInTime)
      assert(hasArrivedWithExpectedVelocity)
      context.self ! msg
      isMaintaingReservation = false
      isTraversing = true
      context.log.info("Starting traversing")
      Behaviors.same

    case msg if isMaintaingReservation && !hasClearLnaeToIntersection =>
      nextIntersectionManager.get ! IntersectionManager.Protocol
        .CancelReservation(reservationDetails.get.reservationId, context.self)
      timers.startSingleTimer(
        PreperingReservation.Timer.RetryReservationRequest,
        TrySendReservationRequest,
        TickSource.timeStep)
      reservationDetails = None
      withVehicle(vehicle.withAccelerationSchedule(None))
      context.self ! msg
      isMaintaingReservation = false
      Behaviors.same

    case Move(replyTo, tickDelta) if isMaintaingReservation =>
      assert(!isTraversing)
      move(replyTo, tickDelta) { () =>
        followCurrentLane()
      }
    case Move(replyTo, tickDelta) if isTraversing =>
      move(replyTo, tickDelta) {
        traverseIntersection(tickDelta)
      }
    case Move(replyTo, tickDelta) =>
      move(replyTo, tickDelta) {
        followCurrentLane().applyBasicThrothelling
      }
  }
}

object Driving {
  trait Protocol {
    case class Move(replyTo: ActorRef[DriversMovementStage.Protocol],
                    tickDelta: TickDelta)
        extends ExtendedProtocol
  }
}
