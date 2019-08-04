package pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.protocol

import java.util.concurrent.TimeUnit

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.AccelerationSchedule
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousVehicleDriver.ExtendedProtocol
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousVehicleDriver
import pl.edu.agh.wmazur.avs.simulation.TickSource.TickDelta
import pl.edu.agh.wmazur.avs.simulation.stage.DriversMovementStage

trait Driving {
  self: AutonomousVehicleDriver =>
  import AutonomousVehicleDriver._

  var accelerationSchedule: Option[AccelerationSchedule] = None

  def move(replyTo: ActorRef[DriversMovementStage.Protocol],
           tickDelta: TickDelta)(
      movementDecidingFn: () => self.type): Behavior[Protocol] = {
    val oldPosition = vehicle.position

    val newPosition = prepareToMove()
      .withVehicle {
        movementDecidingFn().vehicle
          .move(tickDelta.toUnit(TimeUnit.SECONDS))
      }
      .vehicle
      .position

    replyTo ! DriversMovementStage.DriverMoved(context.self,
                                               oldPosition,
                                               newPosition)
    Behaviors.same
  }

  lazy val drive: Behavior[Protocol] = Behaviors.receiveMessagePartial {
    case MovementStep(replyTo, tickDelta) =>
      move(replyTo, tickDelta) {
        applyBasicThrothelling
      }
  }

}

object Driving {
  trait Protocol {
    case class MovementStep(replyTo: ActorRef[DriversMovementStage.Protocol],
                            tickDelta: TickDelta)
        extends ExtendedProtocol
  }
}
