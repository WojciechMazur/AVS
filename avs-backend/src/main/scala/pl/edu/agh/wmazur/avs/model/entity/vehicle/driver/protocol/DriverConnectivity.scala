package pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.protocol

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior, PostStop}
import com.softwaremill.quicklens._
import org.locationtech.spatial4j.shape.{Point, Shape}
import pl.edu.agh.wmazur.avs.model.entity.road.RoadManager.VehicleLanesOccupation
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{Angle, Velocity}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousVehicleDriver
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousVehicleDriver._
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.VehicleDriver.Protocol.IntersectionManagerInRange
import pl.edu.agh.wmazur.avs.simulation.stage.SimulationStateGatherer

trait DriverConnectivity {
  self: AutonomousVehicleDriver =>
  def switchTo(behavior: Behavior[Protocol]): Behavior[Protocol] =
    behavior.orElse(basicConnvectity)

  val basicConnvectity: Behavior[Protocol] = Behaviors
    .receiveMessagePartial[Protocol] {
      case GetPositionReading(replyTo) =>
        replyTo ! PositionReading(
          driverRef = context.self,
          position = vehicle.position,
          heading = vehicle.heading,
          area = vehicle.area
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

          driverGauges = modify(driverGauges)(_.distanceToNextIntersection)
            .setTo(Some(distanceToPoint(position)))
          println("Registered intersection manager")
        }
        ///TODO Should ask for reservation already?
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
    case class GetPositionReading(replyTo: ActorRef[PositionReading])
        extends ExtendedProtocol

    case class PositionReading(
        driverRef: ActorRef[AutonomousVehicleDriver.Protocol],
        position: Point,
        heading: Angle,
        area: Shape)

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
  }
}
