package pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.protocol

import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousVehicleDriver
import pl.edu.agh.wmazur.avs.protocol.SimulationProtocol
import com.softwaremill.quicklens._
import pl.edu.agh.wmazur.avs.EntityRefsGroup
import pl.edu.agh.wmazur.avs.model.entity.utils.SpatialUtils._
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.protocol.DriverConnectivity.VehicleCachedReadings

trait OnTickBehavior {
  self: AutonomousVehicleDriver with DriverConnectivity =>
  import AutonomousVehicleDriver._

  val tickAdapter: ActorRef[SimulationProtocol.Tick] =
    context.messageAdapter[SimulationProtocol.Tick](_ => Tick)

  context.system.receptionist ! Receptionist.register(
    EntityRefsGroup.tickSubscribers,
    tickAdapter)

  val onTickBehavior: Behavior[AutonomousVehicleDriver.Protocol] =
    Behaviors.receiveMessagePartial {
      case Tick =>
        List(
          driverInFront
        ).flatten
          .foreach(_.ref ! GetPositionReading(context.self.narrow))

        Behaviors.same
      case reading: BasicReading =>
        val driverRef = reading.driverRef
        if (driverInFront.exists(_.ref == driverRef)) {
          driverInFront = Some {
            updatedDriverReadings(driverInFront.get, reading)
          }

          driverGauges =
            driverGauges.updateVehicleInFront(driverInFront, vehicle)
        }

        Behaviors.same
    }

  private def updatedDriverReadings(
      cachedReading: VehicleCachedReadings,
      reading: BasicReading): VehicleCachedReadings = {
    cachedReading
      .modify(_.position)
      .setTo(reading.position)
      .modify(_.velocity)
      .setTo(reading.velocity)

  }
}

object OnTickBehavior {
  trait Protocol {
    case object Tick extends AutonomousVehicleDriver.ExtendedProtocol
  }
}
