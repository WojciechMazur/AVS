package pl.edu.agh.wmazur.avs.model.entity.road

import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior, SupervisorStrategy}
import pl.edu.agh.wmazur.avs.model.entity.road.RoadManager._
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousDriver
import pl.edu.agh.wmazur.avs.protocol.SimulationProtocol
import pl.edu.agh.wmazur.avs.simulation.EntityManager
import pl.edu.agh.wmazur.avs.simulation.EntityManager.SpawnResult
import pl.edu.agh.wmazur.avs.simulation.reservation.ReservationArray.Timestamp
import pl.edu.agh.wmazur.avs.simulation.stage.{
  SimulationStateGatherer,
  VehiclesCollectorStage,
  VehiclesSpawnerStage
}
import pl.edu.agh.wmazur.avs.{Agent, EntityRefsGroup, Tick}

import scala.collection.mutable

class RoadManager(
    val context: ActorContext[Protocol],
    val road: Road,
    var oppositeRoadManager: Option[ActorRef[RoadManager.Protocol]] = None)
    extends Agent[Protocol] {
  val roadSpawnerWorker: ActorRef[RoadSpawnerWorker.Protocol] = context.spawn(
    RoadSpawnerWorker.init(context.self, road.lanes),
    s"road-spawner-${road.id}"
  )
  val roadCollectorWorker: ActorRef[RoadCollectorWorker.Protocol] = context
    .spawn(RoadCollectorWorker.init(context.self, road.lanes),
           s"road-collector-${road.id}")

  context.system.receptionist ! Receptionist.register(EntityRefsGroup.road,
                                                      context.self)

  val vehiclesAtLanes
    : mutable.Map[Lane, Set[ActorRef[AutonomousDriver.ExtendedProtocol]]] =
    road.lanes
      .map(_ -> Set.empty[ActorRef[AutonomousDriver.ExtendedProtocol]])(
        collection.breakOut)

  override protected val initialBehaviour: Behavior[Protocol] =
    Behaviors.receiveMessage {
      case TrySpawn(replyTo, entityManagerRef, currentTime) =>
        roadSpawnerWorker ! RoadSpawnerWorker.TrySpawn(replyTo,
                                                       entityManagerRef,
                                                       vehiclesAtLanes.toMap,
                                                       currentTime)
        Behaviors.same

      case TryCollect(replyTo, entityManagerRef, currentTime) =>
        roadCollectorWorker ! RoadCollectorWorker.TryCollect(
          replyTo,
          entityManagerRef,
          vehiclesAtLanes.toMap)
        Behaviors.same

      case RegisterOppositeRoadManager(roadManagerRef) =>
        oppositeRoadManager = Some(roadManagerRef)
        context.watchWith(roadManagerRef,
                          OppositeRoadTerminated(roadManagerRef))
        Behaviors.same
      case OppositeRoadTerminated(ref) =>
        oppositeRoadManager = None
        Behaviors.same

      case GetDetailedReadings(replyTo) =>
        replyTo ! SimulationStateGatherer.RoadDetailedReading(context.self,
                                                              road)
        Behaviors.same
      case vlo @ VehicleLanesOccupation(driver, _, _) =>
        for {
          leavedLane <- vlo.leavedLanes
          lastState = vehiclesAtLanes.getOrElse(leavedLane, Set.empty)
        } vehiclesAtLanes.update(leavedLane, lastState - driver)
        for {
          enteredLane <- vlo.enteredLanes
          lastState = vehiclesAtLanes.getOrElse(enteredLane, Set.empty)
        } vehiclesAtLanes.update(enteredLane, lastState + driver)
        Behaviors.same

      case GetLanesOccupation(replyTo) =>
        replyTo ! LanesOccupation(context.self, vehiclesAtLanes.toMap)

        Behaviors.same
    }
}

object RoadManager {
  sealed trait Protocol extends SimulationProtocol
  case class TrySpawn(replyTo: ActorRef[VehiclesSpawnerStage.Protocol],
                      entityManagerRef: ActorRef[EntityManager.Protocol],
                      currentTime: Timestamp)
      extends Protocol
  case class TryCollect(replyTo: ActorRef[VehiclesCollectorStage.Protocol],
                        entityManagerRef: ActorRef[EntityManager.Protocol],
                        currentTime: Timestamp)
      extends Protocol

  case class RegisterOppositeRoadManager(
      roadManager: ActorRef[RoadManager.Protocol])
      extends Protocol

  case class OppositeRoadTerminated(ref: ActorRef[RoadManager.Protocol])
      extends Protocol

  case class GetDetailedReadings(
      replyTo: ActorRef[SimulationStateGatherer.Protocol])
      extends Protocol

  case class GetLanesOccupation(replyTo: ActorRef[LanesOccupation])
      extends Protocol
  case class LanesOccupation(
      roadManagerRef: ActorRef[RoadManager.Protocol],
      vehiclesAtLanes: Map[Lane,
                           Set[ActorRef[AutonomousDriver.ExtendedProtocol]]])

  case class VehicleLanesOccupation(
      driver: ActorRef[AutonomousDriver.ExtendedProtocol],
      lanes: Set[Lane],
      previousLanes: Set[Lane] = Set.empty)
      extends Protocol {
    lazy val enteredLanes: Set[Lane] = lanes.diff(previousLanes)
    lazy val leavedLanes: Set[Lane] = previousLanes.diff(lanes)
  }

  def init(
      optId: Option[Road#Id],
      lanes: List[Lane],
      oppositeRoadRef: Option[ActorRef[RoadManager.Protocol]],
      replyTo: Option[ActorRef[EntityManager.SpawnResult[Road, Protocol]]] =
        None): Behavior[Protocol] =
    Behaviors.setup { ctx =>
      val road = Road(optId, lanes, ctx.self)
      replyTo.foreach(_ ! SpawnResult(road, ctx.self))

      new RoadManager(ctx, road, oppositeRoadRef)

    }

}
