package pl.edu.agh.wmazur.avs.model.entity.vehicle.driver
import java.util.concurrent.TimeUnit

import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior, PostStop}
import org.locationtech.spatial4j.shape.{Point, Shape}
import pl.edu.agh.wmazur.avs.model.entity.road.RoadManager.VehicleLanesOccupation
import pl.edu.agh.wmazur.avs.model.entity.road.{Lane, Road}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{Angle, Velocity}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousDriver._
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.VehicleDriver.Protocol.IntersectionManagerInRange
import pl.edu.agh.wmazur.avs.model.entity.vehicle.{
  BasicVehicle,
  Vehicle,
  VehicleSpec
}
import pl.edu.agh.wmazur.avs.simulation.EntityManager.SpawnResult
import pl.edu.agh.wmazur.avs.simulation.TickSource.TickDelta
import pl.edu.agh.wmazur.avs.simulation.stage.{
  DriversMovementStage,
  SimulationStateGatherer
}
import pl.edu.agh.wmazur.avs.{Agent, EntityRefsGroup}

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
            heading = vehicle.heading,
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

        case IntersectionManagerInRange(ref, position) =>
          if (nextIntersectionManager.isEmpty) {
            nextIntersectionManager = Some(ref)
            nextIntersectionPosition = Some(position)
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

  override protected def withVehicle(vehicle: Vehicle): VehicleDriver = {
    vehicle match {
      case bv: BasicVehicle => this.vehicle = bv
      case _                => throw new RuntimeException("Unsupported operation")
    }
    this
  }
}

object AutonomousDriver {
  type Protocol = VehicleDriver.Protocol
  sealed trait ExtendedProtocol extends VehicleDriver.Protocol

  case class GetPositionReading(replyTo: ActorRef[PositionReading])
      extends ExtendedProtocol

  case class PositionReading(
      driverRef: ActorRef[AutonomousDriver.ExtendedProtocol],
      position: Point,
      heading: Angle,
      area: Shape)
      extends ExtendedProtocol

  case class GetDetailedReadings(
      replyTo: ActorRef[SimulationStateGatherer.Protocol])
      extends ExtendedProtocol

  case class MovementStep(replyTo: ActorRef[DriversMovementStage.Protocol],
                          tickDelta: TickDelta)
      extends ExtendedProtocol

  def init(
      id: Vehicle#Id,
      spec: VehicleSpec,
      position: Point,
      heading: Angle,
      velocity: Velocity,
      lane: Lane,
      replyTo: Option[ActorRef[SpawnResult[Vehicle, VehicleDriver.Protocol]]] =
        None): Behavior[ExtendedProtocol] = {
    Behaviors.setup[VehicleDriver.Protocol] { ctx =>
      val vehicle = BasicVehicle(driverRef = ctx.self,
                                 spec = spec,
                                 position = position,
                                 heading = heading,
                                 velocity = velocity)
      replyTo.foreach(_ ! SpawnResult(vehicle, ctx.self.unsafeUpcast))
      new AutonomousDriver(context = ctx, spawnLane = lane, vehicle = vehicle)
    }
  }.narrow

}
