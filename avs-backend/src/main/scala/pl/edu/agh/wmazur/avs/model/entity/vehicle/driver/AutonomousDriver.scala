package pl.edu.agh.wmazur.avs.model.entity.vehicle.driver
import java.util.concurrent.TimeUnit

import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior, PostStop}
import org.locationtech.spatial4j.shape.{Point, Shape}
import pl.edu.agh.wmazur.avs.model.entity.intersection.IntersectionManager
import pl.edu.agh.wmazur.avs.model.entity.road.RoadManager.VehicleLanesOccupation
import pl.edu.agh.wmazur.avs.model.entity.road.{Lane, Road}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{Angle, Velocity}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousDriver._
import pl.edu.agh.wmazur.avs.model.entity.vehicle.{
  BasicVehicle,
  Vehicle,
  VehicleSpec
}
import pl.edu.agh.wmazur.avs.protocol.SimulationProtocol
import pl.edu.agh.wmazur.avs.simulation.TickSource.TickDelta
import pl.edu.agh.wmazur.avs.simulation.stage.{
  DriversMovementStage,
  SimulationStateGatherer
}
import pl.edu.agh.wmazur.avs.{Agent, Dimension, EntityRefsGroup}

import scala.collection.mutable
class AutonomousDriver(val context: ActorContext[Protocol],
                       spawnLane: Lane,
                       var vehicle: BasicVehicle,
) extends Agent[Protocol]
    with VehicleDriver {

  override val occupiedLanes: mutable.Set[Lane] = mutable.Set.empty
  override var currentLane: Lane = setCurrentLane(spawnLane)
  context.system.receptionist ! Receptionist.register(EntityRefsGroup.driver,
                                                      context.self)

  var destination: Option[Road] = None
  var prevIntersectionManager: Option[IntersectionManager] = None
  var nextIntersectionManager: Option[IntersectionManager] = None
  var distanceToNextIntersection: Option[Dimension] = None
  var distanceToPrevIntersection: Option[Dimension] = None

  override protected def setCurrentLane(lane: Lane): Lane = {
    val previousState = occupiedLanes.toSet
    super.setCurrentLane(lane)
    val currentState = occupiedLanes.toSet
    lane.spec.road.foreach(
      _.managerRef ! VehicleLanesOccupation(context.self,
                                            currentState,
                                            previousState))
    lane
  }
  override protected val initialBehaviour: Behavior[Protocol] =
    Behaviors
      .receiveMessagePartial[Protocol] {
        case GetPositionReading(replyTo) =>
          replyTo ! PositionReading(
            driverRef = context.self,
            position = vehicle.position,
            area = vehicle.area
          )
          Behaviors.same

        case GetDetailedReadings(replyTo) =>
          replyTo ! SimulationStateGatherer.DriverDetailedReading(context.self,
                                                                  vehicle)
          Behaviors.same

        case MovementStep(replyTo, tickDelta) =>
          val oldPosition = vehicle.position
          this.vehicle = vehicle
            .move(tickDelta.toUnit(TimeUnit.SECONDS))
            .asInstanceOf[BasicVehicle]
          val newPosition = vehicle.position

          replyTo ! DriversMovementStage.DriverMoved(context.self,
                                                     oldPosition,
                                                     newPosition)
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

  override protected def withVehicle(vehicle: Vehicle): VehicleDriver = {
    vehicle match {
      case bv: BasicVehicle => this.vehicle = bv
      case _                => throw new RuntimeException("Unsupported operation")
    }
    this
  }
}

object AutonomousDriver {
  sealed trait Protocol extends SimulationProtocol
  case class GetPositionReading(replyTo: ActorRef[PositionReading])
      extends Protocol
  case class PositionReading(driverRef: ActorRef[AutonomousDriver.Protocol],
                             position: Point,
                             area: Shape)
      extends Protocol
  case class GetDetailedReadings(
      replyTo: ActorRef[SimulationStateGatherer.Protocol])
      extends Protocol
  case class MovementStep(replyTo: ActorRef[DriversMovementStage.Protocol],
                          tickDelta: TickDelta)
      extends Protocol

  def apply(id: Vehicle#Id,
            spec: VehicleSpec,
            position: Point,
            heading: Angle,
            velocity: Velocity,
            lane: Lane): Behavior[Protocol] = {
    Behaviors.setup { ctx =>
      val vehicle = BasicVehicle(driverRef = ctx.self,
                                 spec = spec,
                                 position = position,
                                 heading = heading,
                                 velocity = velocity)
      new AutonomousDriver(context = ctx, spawnLane = lane, vehicle = vehicle)
    }
  }
}
