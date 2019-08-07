package pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.protocol

import java.util.concurrent.TimeUnit

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
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

  lazy val drive: Behavior[Protocol] = Behaviors.receiveMessagePartial {
    case Move(replyTo, tickDelta)
        if hasOngoingRequest &&
          vehicle.accelerationSchedule.isDefined =>
      move(replyTo, tickDelta) {
        controlScheduler
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
