package pl.edu.agh.wmazur.avs.simulation.stage

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import com.softwaremill.quicklens._
import org.locationtech.spatial4j.shape.Shape
import pl.edu.agh.wmazur.avs.model.entity.intersection.{
  Intersection,
  IntersectionManager
}
import pl.edu.agh.wmazur.avs.model.entity.road.{Lane, Road, RoadManager}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.Vehicle
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousDriver
import pl.edu.agh.wmazur.avs.model.state.SimulationState
import pl.edu.agh.wmazur.avs.simulation.SimulationManager
import pl.edu.agh.wmazur.avs.simulation.SimulationManager.Protocol.StateUpdate

import scala.concurrent.duration.FiniteDuration
object SimulationStateGatherer {
  sealed trait Protocol

  case class GetCurrentState(
      replyTo: ActorRef[SimulationManager.Protocol],
      roadRefs: Set[ActorRef[RoadManager.Protocol]],
      driverRefs: Set[ActorRef[AutonomousDriver.ExtendedProtocol]],
      intersectionRefs: Set[ActorRef[IntersectionManager.Protocol]],
      currentTime: Long,
      tickDelta: FiniteDuration)
      extends Protocol

  case class DriverDetailedReading(
      driverRef: ActorRef[AutonomousDriver.ExtendedProtocol],
      vehicle: Vehicle,
  ) extends Protocol
  case class DriverNotExists(
      driverRef: ActorRef[AutonomousDriver.ExtendedProtocol])
      extends Protocol

  case class RoadDetailedReading(roadRef: ActorRef[RoadManager.Protocol],
                                 road: Road)
      extends Protocol

  case class IntersectionDetailedReading(
      intersectionRef: ActorRef[IntersectionManager.Protocol],
      intersection: Intersection)
      extends Protocol

  final lazy val init: Behavior[Protocol] = Behaviors.setup { ctx =>
    ctx.log.info("Simulation state gatharer stage spawned")
    idle
  }

  private val idle: Behaviors.Receive[Protocol] =
    Behaviors.receive {
      case (ctx,
            GetCurrentState(replyTo,
                            roadRefs,
                            driverRefs,
                            intersectionRefs,
                            currentTime,
                            tickDelta)) =>
        dispatchRequests(ctx, roadRefs, intersectionRefs, driverRefs)

        val emptyState = SimulationState.empty
          .copy(currentTime = currentTime, tickDelta = tickDelta)
        gatherState(replyTo, roadRefs, intersectionRefs, driverRefs, emptyState)
    }

  private def dispatchRequests(
      context: ActorContext[SimulationStateGatherer.Protocol],
      roadRefs: Set[ActorRef[RoadManager.Protocol]],
      intersectionRefs: Set[ActorRef[IntersectionManager.Protocol]],
      driverRefs: Set[ActorRef[AutonomousDriver.ExtendedProtocol]]): Unit = {
    for {
      request <- Some(RoadManager.GetDetailedReadings(context.self))
      roadRef <- roadRefs
    } roadRef ! request

    for {
      request <- Some(AutonomousDriver.GetDetailedReadings(context.self))
      driverRef <- driverRefs
      _ = context.watchWith(driverRef, DriverNotExists(driverRef))
    } driverRef ! request

    for {
      request <- Some(IntersectionManager.GetDetailedReadings(context.self))
      ref <- intersectionRefs
    } ref ! request
  }

  private def gatherState(
      replyTo: ActorRef[SimulationManager.Protocol],
      roadRefs: Set[ActorRef[RoadManager.Protocol]],
      intersectionRefs: Set[ActorRef[IntersectionManager.Protocol]],
      driverRefs: Set[ActorRef[AutonomousDriver.ExtendedProtocol]],
      state: SimulationState = SimulationState.empty): Behavior[Protocol] = {
    val finished = roadRefs.isEmpty && driverRefs.isEmpty && intersectionRefs.isEmpty

    if (!finished) {
      Behaviors.receivePartial {

        case (ctx, DriverDetailedReading(driverRef, vehicle)) =>
          ctx.unwatch(driverRef)
          val newState =
            modify(state)(_.vehicles)
              .setTo(state.vehicles.updated(vehicle.id, vehicle))
          gatherState(replyTo,
                      roadRefs,
                      intersectionRefs,
                      driverRefs - driverRef,
                      newState)

        case (_, DriverNotExists(driverRef)) =>
          gatherState(replyTo,
                      roadRefs,
                      intersectionRefs,
                      driverRefs - driverRef,
                      state)

        case (_, RoadDetailedReading(roadRef, road)) =>
          val newState = modify(state)(_.roads)
            .setTo(state.roads.updated(road.id, road))
          gatherState(replyTo,
                      roadRefs - roadRef,
                      intersectionRefs,
                      driverRefs,
                      newState)

        case (_, IntersectionDetailedReading(intersectionRef, intersection)) =>
          val newState = modify(state)(_.intersections)
            .setTo(state.intersections.updated(intersection.id, intersection))
          gatherState(replyTo,
                      roadRefs,
                      intersectionRefs - intersectionRef,
                      driverRefs,
                      newState)
      }
    } else {
      replyTo ! StateUpdate(state)
      idle
    }
  }

}
