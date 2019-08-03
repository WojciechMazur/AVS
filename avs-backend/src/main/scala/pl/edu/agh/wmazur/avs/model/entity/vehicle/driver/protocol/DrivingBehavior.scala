package pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.protocol

import java.util.concurrent.TimeUnit

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousVehicleDriver
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousVehicleDriver.ExtendedProtocol
import pl.edu.agh.wmazur.avs.simulation.TickSource.TickDelta
import pl.edu.agh.wmazur.avs.simulation.stage.DriversMovementStage

trait DrivingBehavior {
  self: AutonomousVehicleDriver with DriverConnectivity =>
  import AutonomousVehicleDriver._

  def drive: Behavior[Protocol] = Behaviors.receiveMessagePartial {
    case MovementStep(replyTo, tickDelta) =>
      val oldPosition = vehicle.position

      withVehicle(
        updateGauges().applyBasicThrothelling
          .prepareToMove()
          .vehicle
          .move(tickDelta.toUnit(TimeUnit.SECONDS)))
      val newPosition = vehicle.position

      replyTo ! DriversMovementStage.DriverMoved(context.self,
                                                 oldPosition,
                                                 newPosition)
      Behaviors.same
  }

}

object DrivingBehavior {
  trait Protocol {
    case class MovementStep(replyTo: ActorRef[DriversMovementStage.Protocol],
                            tickDelta: TickDelta)
        extends ExtendedProtocol
  }
}
