package pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.protocol

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior, PostStop}
import com.softwaremill.quicklens._
import org.locationtech.jts.geom.Geometry
import org.locationtech.spatial4j.shape.Point
import pl.edu.agh.wmazur.avs.model.entity.road.RoadManager
import pl.edu.agh.wmazur.avs.model.entity.road.RoadManager.VehicleLanesOccupation
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{
  Acceleration,
  Angle,
  Velocity
}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousVehicleDriver
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousVehicleDriver.{
  Protocol,
  _
}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.VehicleDriver.Protocol.IntersectionManagerInRange
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.protocol.DriverConnectivity.{
  NeighbourFetchingInterval,
  VehicleCachedReadings
}
import pl.edu.agh.wmazur.avs.simulation.stage.SimulationStateGatherer

import scala.concurrent.duration._

trait DriverConnectivity {
  self: AutonomousVehicleDriver =>

  timers.startPeriodicTimer(FetchNeighbourVehicles,
                            FetchNeighbourVehicles,
                            NeighbourFetchingInterval.whenNone)

  var driverInFront: Option[VehicleCachedReadings] = None

  lazy val basicConnectivity: Behavior[Protocol] = Behaviors
    .receiveMessagePartial[Protocol] {
      case GetPositionReading(replyTo) =>
        replyTo ! BasicReading(
          driverRef = context.self,
          position = vehicle.position,
          heading = vehicle.heading,
          velocity = vehicle.velocity,
          acceleration = vehicle.acceleration,
          geometry = vehicle.geometry
        )
        Behaviors.same

      case GetDetailedReadings(replyTo) =>
        replyTo ! SimulationStateGatherer.DriverDetailedReading(context.self,
                                                                vehicle)
        Behaviors.same

      case IntersectionManagerInRange(ref, position) =>
        if (nextIntersectionManager.isEmpty) {
          nextIntersectionManager = Some(ref)
          nextIntersectionPosition = Some(position)

          driverGauges = driverGauges
            .modify(_.distanceToNextIntersection)
            .setTo(Some(distanceToPoint(position)))

          context.self ! AskForMaximalCrossingVelocities
        }
        Behaviors.same

      case FetchNeighbourVehicles =>
        currentLane.spec.road.get.managerRef ! RoadManager.FindPrecedingVehicle(
          context.self)
        Behaviors.same

      case result: PrecedingVehicleResult =>
        result match {
          case NotFoundPrecedingVehicle =>
            if (driverInFront.nonEmpty) {
              timers.cancel(FetchNeighbourVehicles)
              timers.startPeriodicTimer(FetchNeighbourVehicles,
                                        FetchNeighbourVehicles,
                                        NeighbourFetchingInterval.whenNone)
              driverInFront = None
              driverGauges.updateVehicleInFront(None, vehicle)
            }
          case FoundPrecedingVehicle(vehicleRef, position, velocity) =>
            if (driverInFront.isEmpty) {
              timers.cancel(FetchNeighbourVehicles)
              timers.startPeriodicTimer(FetchNeighbourVehicles,
                                        FetchNeighbourVehicles,
                                        NeighbourFetchingInterval.whenSome)
            }
            driverInFront = Some {
              VehicleCachedReadings(
                ref = vehicleRef,
                position = position,
                velocity = velocity,
                geometry = None
              )
            }
            driverGauges =
              driverGauges.updateVehicleInFront(driverInFront, vehicle)
        }
        Behaviors.same

    }
    .receiveSignal {
      case (_, PostStop) =>
        for {
          road <- currentLane.spec.road
          roadManager = road.managerRef
          occupiedByVehicle = occupiedLanes.toSet + currentLane
          vloChanged = VehicleLanesOccupation(context.self,
                                              Set.empty,
                                              occupiedByVehicle)
        } roadManager ! vloChanged

        Behaviors.stopped
    }

}

object DriverConnectivity {
  trait Protocol {
    self: AutonomousVehicleDriver.type =>

    case class GetPositionReading(replyTo: ActorRef[BasicReading])
        extends ExtendedProtocol

    case class BasicReading(
        driverRef: ActorRef[AutonomousVehicleDriver.Protocol],
        position: Point,
        heading: Angle,
        velocity: Velocity,
        acceleration: Acceleration,
        geometry: Geometry)
        extends Protocol

    case class GetDetailedReadings(
        replyTo: ActorRef[SimulationStateGatherer.Protocol])
        extends ExtendedProtocol

    sealed trait PrecedingVehicleResult extends ExtendedProtocol
    case object NotFoundPrecedingVehicle extends PrecedingVehicleResult
    case class FoundPrecedingVehicle(
        vehicleRef: ActorRef[AutonomousVehicleDriver.Protocol],
        position: Point,
        velocity: Velocity)
        extends PrecedingVehicleResult

    case object FetchNeighbourVehicles extends Protocol
    case object AskForMaximalCrossingVelocities extends Protocol
  }

  object NeighbourFetchingInterval {
    val whenNone: FiniteDuration = 100.millis
    val whenSome: FiniteDuration = 250.millis
  }

  case class VehicleCachedReadings(
      ref: ActorRef[AutonomousVehicleDriver.Protocol],
      position: Point,
      velocity: Velocity,
      geometry: Option[Geometry]
  )
}
