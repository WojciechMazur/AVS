package pl.edu.agh.wmazur.avs.model.entity.road.workers

import akka.actor.typed.{ActorRef, Behavior, Terminated}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import org.locationtech.spatial4j.shape.Point
import pl.edu.agh.wmazur.avs.model.entity.road.Lane
import pl.edu.agh.wmazur.avs.model.entity.road.RoadManager.Protocol
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.Velocity
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousVehicleDriver

import scala.collection.mutable

object RoadVehiclesCoordinator {
  // format: off
  sealed trait Protocol
  case class FindPrecedingVehicle(
      replyTo: ActorRef[AutonomousVehicleDriver.Protocol])
      extends Protocol

  case class VehiclePositionReading(
      driverRef: ActorRef[AutonomousVehicleDriver.Protocol],
      position: Point,
      velocity: Velocity)
      extends Protocol

  case class VehiclesOccupationUpdate(
      driverAtLane: Map[Lane, Set[ActorRef[AutonomousVehicleDriver.Protocol]]])
      extends Protocol
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
      driversAtLanes: mutable.Map[Lane,Set[ActorRef[AutonomousVehicleDriver.Protocol]]],
      driversState: mutable.Map[ActorRef[AutonomousVehicleDriver.Protocol],
                                DriverCachedState]) {
  // format: on
    def removeDriver(
        ref: ActorRef[AutonomousVehicleDriver.Protocol]): Context = {
      driversState.remove(ref)

      driversAtLanes
        .filter(_._2.contains(ref))
        .keySet
        .foreach { lane =>
          val set = driversAtLanes(lane)
          driversAtLanes.update(lane, set - ref)
        }
      this
    }

    def updateDriver(driver: ActorRef[AutonomousVehicleDriver.Protocol],
                     state: DriverCachedState): Context = {
      val updatedSet = driversAtLanes.getOrElse(state.currentLane, Set.empty) + driver
      driversAtLanes.update(state.currentLane, updatedSet)

      driversState.update(driver, state)
      this
    }

  }

  def init: Behavior[Protocol] = Behaviors.setup { ctx =>
    val context = Context(context = ctx,
                          driversAtLanes = mutable.Map.empty,
                          driversState = mutable.Map.empty)

    idle(context)
  }

  private def idle(context: Context): Behavior[Protocol] =
    Behaviors
      .receiveMessagePartial[Protocol] {
        case FindPrecedingVehicle(replyTo) =>
          val precedingVehicleState = for {
            driverState <- context.driversState.get(replyTo)
            lane = driverState.currentLane
            drivers <- context.driversAtLanes.get(lane)
            driversWithState = drivers
              .map(context.driversState)
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

          Behaviors.same
        //TODO Integracja z RoadManagerem oraz zdobywanie informacji o statusie pojazdów
      }
      .receiveSignal {
        case (_,
              Terminated(
                ref: ActorRef[AutonomousVehicleDriver.Protocol] @unchecked)) =>
          idle {
            context.removeDriver(ref)
          }
      }
}
