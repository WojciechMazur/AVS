package pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.protocol

import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import com.softwaremill.quicklens._
import pl.edu.agh.wmazur.avs.EntityRefsGroup
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousVehicleDriver
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.protocol.DriverConnectivity.VehicleCachedReadings
import pl.edu.agh.wmazur.avs.protocol.SimulationProtocol
import pl.edu.agh.wmazur.avs.model.entity.intersection.reservation.ReservationArray.Timestamp

trait OnTick {
  self: AutonomousVehicleDriver =>
  import AutonomousVehicleDriver._

  var currentTime: Timestamp = 0L

  val tickAdapter: ActorRef[SimulationProtocol.Tick] =
    context.messageAdapter[SimulationProtocol.Tick] {
      case SimulationProtocol.Tick.Default(time, _, _) => Tick(time)
    }

  context.system.receptionist ! Receptionist.register(
    EntityRefsGroup.tickSubscribers,
    tickAdapter)

  lazy val onTickBehavior: Behavior[AutonomousVehicleDriver.Protocol] =
    Behaviors.receiveMessagePartial {
      case Tick(time) =>
        currentTime = time
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

object OnTick {
  trait Protocol {
    case class Tick(currentTime: Timestamp)
        extends AutonomousVehicleDriver.ExtendedProtocol
  }
}
