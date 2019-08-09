package pl.edu.agh.wmazur.avs.simulation.stage

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import org.locationtech.spatial4j.shape.Point
import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.road.RoadManager
import pl.edu.agh.wmazur.avs.model.entity.utils.SpatialUtils
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousVehicleDriver
import pl.edu.agh.wmazur.avs.protocol.SimulationProtocol
import pl.edu.agh.wmazur.avs.simulation.SimulationManager
import pl.edu.agh.wmazur.avs.simulation.SimulationManager.Protocol.Done
import pl.edu.agh.wmazur.avs.model.entity.intersection.reservation.ReservationArray.Timestamp

import scala.concurrent.duration.FiniteDuration

object DriversMovementStage {
  sealed trait Protocol extends SimulationProtocol
  case class Step(
      replyTo: ActorRef[SimulationManager.Protocol],
      roadRefs: Set[ActorRef[RoadManager.Protocol]],
      driversRefs: Set[ActorRef[AutonomousVehicleDriver.ExtendedProtocol]],
      currentTime: Timestamp,
      tickDelta: FiniteDuration)
      extends Protocol
  case class DriverMoved(
      driverRef: ActorRef[AutonomousVehicleDriver.ExtendedProtocol],
      oldPosition: Point,
      newPosition: Point)
      extends Protocol {
    lazy val distanceTraveled: Dimension =
      SpatialUtils.shapeFactory.getSpatialContext
        .calcDistance(oldPosition, newPosition)
  }
  case class DriverLost(
      driverRef: ActorRef[AutonomousVehicleDriver.ExtendedProtocol])
      extends Protocol

  final lazy val init: Behavior[Protocol] = Behaviors.setup { ctx =>
    ctx.log.info("Drivers movement stage spawned")
    idle
  }

  private val idle: Behaviors.Receive[Protocol] =
    Behaviors.receivePartial {
      case (ctx, Step(replyTo, _, driverRefs, _, tickDelta)) =>
        driverRefs.foreach { ref =>
          ctx.watchWith(ref, DriverLost(ref))
          ref ! AutonomousVehicleDriver.Move(ctx.self, tickDelta)
        }
        waitForResponses(replyTo, driverRefs)
    }

  private def waitForResponses(
      replyTo: ActorRef[SimulationManager.Protocol],
      awaiting: Set[ActorRef[AutonomousVehicleDriver.ExtendedProtocol]])
    : Behaviors.Receive[Protocol] = {
    if (awaiting.isEmpty) {
      replyTo ! Done
      idle
    } else {
      Behaviors.receivePartial {
        case (ctx, DriverMoved(driverRef, _, _)) =>
          ctx.unwatch(driverRef)
          waitForResponses(replyTo, awaiting - driverRef)
        case (_, DriverLost(driverRef)) =>
          waitForResponses(replyTo, awaiting - driverRef)
      }
    }
  }

}
