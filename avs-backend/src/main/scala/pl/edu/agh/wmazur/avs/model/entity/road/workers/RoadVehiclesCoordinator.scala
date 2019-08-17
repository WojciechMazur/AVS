package pl.edu.agh.wmazur.avs.model.entity.road.workers

import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior, Terminated}
import org.locationtech.spatial4j.shape.Point
import pl.edu.agh.wmazur.avs.EntityRefsGroup
import pl.edu.agh.wmazur.avs.model.entity.road.Lane
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.Velocity
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousVehicleDriver
import pl.edu.agh.wmazur.avs.protocol.SimulationProtocol

import scala.collection.{Set, mutable}

object RoadVehiclesCoordinator {
  // format: off
  sealed trait Protocol extends SimulationProtocol

  object Protocol extends SimulationProtocol {
    case class FindPrecedingVehicle(replyTo: ActorRef[AutonomousVehicleDriver.Protocol])
      extends Protocol
    case class VehiclePositionReading( driverRef: ActorRef[AutonomousVehicleDriver.Protocol],
                                       position: Point,
                                       velocity: Velocity)
      extends Protocol
    case class VehiclesOccupationUpdate( driverRef: ActorRef[AutonomousVehicleDriver.Protocol],
                                         enteredLanes: Set[Lane],
                                         exitedLanes: Set[Lane])
      extends Protocol
  }
  case object Tick extends Protocol
  import Protocol._
  // format: on

  private case class DriverCachedState(
      ref: ActorRef[AutonomousVehicleDriver.Protocol],
      position: Point,
      velocity: Velocity,
      currentLane: Lane) {
    def normalizedDistanceAlongLane: Double =
      currentLane.normalizedDistanceAlongLane(position)
  }

  // format: off
  private case class Context(
      context: ActorContext[Protocol],
      driversAtLanes: mutable.Map[Lane, mutable.Set[ActorRef[AutonomousVehicleDriver.Protocol]]],
      driversOccupation: mutable.Map[ActorRef[AutonomousVehicleDriver.Protocol], Set[Lane]],
      driversState: mutable.Map[ActorRef[AutonomousVehicleDriver.Protocol], DriverCachedState],
      driversAdapter: ActorRef[driver.AutonomousVehicleDriver.BasicReading]) {
    // format: on
    def removeDriver(
        ref: ActorRef[AutonomousVehicleDriver.Protocol]): Context = {

      driversAtLanes
        .filterKeys(driversOccupation(ref).contains)
        .keySet
        .foreach { lane =>
          val set = driversAtLanes(lane)
          set -= ref
        }

      driversState.remove(ref)
      driversOccupation.remove(ref)
      this
    }

    def updateDriverOccupation(
        driver: ActorRef[AutonomousVehicleDriver.Protocol],
        enteredLanes: Set[Lane],
        leavedLanes: Set[Lane]): Context = {
      enteredLanes.foreach { lane =>
        driversAtLanes
          .getOrElseUpdate(lane, mutable.Set.empty)
          .update(driver, included = true)
      }

      leavedLanes.foreach { lane =>
        driversAtLanes
          .getOrElseUpdate(lane, mutable.Set.empty)
          .update(driver, included = false)
      }
      driversOccupation.update(
        driver,
        driversOccupation
          .getOrElse(driver, Set.empty) ++ enteredLanes -- leavedLanes)
      this
    }

    def updateDriverState(driver: ActorRef[AutonomousVehicleDriver.Protocol],
                          state: DriverCachedState): Context = {

      driversState.update(driver, state)
      this
    }

  }

  def init(lanes: List[Lane]): Behavior[Protocol] = Behaviors.setup { ctx =>
    val driversAdapter =
      ctx.messageAdapter[AutonomousVehicleDriver.BasicReading] {
        case AutonomousVehicleDriver.BasicReading(ref,
                                                  position,
                                                  velocity,
                                                  _,
                                                  _,
                                                  _) =>
          VehiclePositionReading(ref, position, velocity)
      }

    val tickAdapter = ctx.messageAdapter[SimulationProtocol.Tick] {
      _: SimulationProtocol.Tick =>
        Tick
    }

    val context = Context(context = ctx,
                          driversAtLanes = mutable.Map.empty,
                          driversOccupation = mutable.Map.empty,
                          driversState = mutable.Map.empty,
                          driversAdapter = driversAdapter)

    ctx.system.receptionist ! Receptionist
      .register(EntityRefsGroup.tickSubscribers, tickAdapter)

    active(context)
  }

  private def active(context: Context): Behavior[Protocol] =
    Behaviors
      .receiveMessage[Protocol] {
        case FindPrecedingVehicle(replyTo) =>
          val precedingVehicleState = for {
            driverState <- context.driversState.get(replyTo)
            lane = driverState.currentLane
            drivers <- context.driversAtLanes.get(lane)
            driversWithState = drivers
              .flatMap(context.driversState.get)
              .toVector
              .sortBy(_.normalizedDistanceAlongLane)
            driversPrecedingVehicle = driversWithState
              .dropWhile(_.ref != replyTo)
              .tail
            nextVehicleState <- driversPrecedingVehicle.headOption
          } yield nextVehicleState

          precedingVehicleState match {
            case Some(DriverCachedState(ref, position, velocity, _)) =>
              replyTo ! AutonomousVehicleDriver.FoundPrecedingVehicle(
                vehicleRef = ref,
                position = position,
                velocity = velocity)
            case None =>
              replyTo ! AutonomousVehicleDriver.NotFoundPrecedingVehicle
          }
          active(context)

        case Tick =>
          for {
            readingRequest <- Some(
              AutonomousVehicleDriver.GetPositionReading(
                context.driversAdapter))
            driverRef <- context.driversOccupation.keySet

          } driverRef ! readingRequest

          active(context)

        case VehiclesOccupationUpdate(driverRef, enteredLanes, exitedLanes) =>
          context.context.watch(driverRef)
          active {
            context.updateDriverOccupation(driverRef, enteredLanes, exitedLanes)
          }

        case VehiclePositionReading(driverRef, position, velocity) =>
          val currentLane = context.driversOccupation(driverRef) match {
            case lanes if lanes.tail.isEmpty => lanes.head
            case lanes                       => lanes.find(_.area.relate(position).intersects()).get
          }

          active {
            context.updateDriverState(driverRef,
                                      DriverCachedState(ref = driverRef,
                                                        position = position,
                                                        velocity = velocity,
                                                        currentLane =
                                                          currentLane))
          }
      }
      .receiveSignal {
        case (_,
              Terminated(
                ref: ActorRef[AutonomousVehicleDriver.Protocol] @unchecked)) =>
          active {
            context.removeDriver(ref)
          }
      }
}
