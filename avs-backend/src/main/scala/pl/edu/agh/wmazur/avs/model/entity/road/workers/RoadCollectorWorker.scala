package pl.edu.agh.wmazur.avs.model.entity.road.workers

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import pl.edu.agh.wmazur.avs.Agent
import pl.edu.agh.wmazur.avs.model.entity.road.workers.RoadCollectorWorker.{
  LaneCollectResult,
  Protocol,
  TryCollect
}
import pl.edu.agh.wmazur.avs.model.entity.road.{Lane, RoadManager}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousVehicleDriver
import pl.edu.agh.wmazur.avs.protocol.SimulationProtocol
import pl.edu.agh.wmazur.avs.simulation.EntityManager
import pl.edu.agh.wmazur.avs.simulation.stage.VehiclesCollectorStage

import scala.collection.mutable

class RoadCollectorWorker(
    val context: ActorContext[Protocol],
    roadManagerRef: ActorRef[RoadManager.Protocol],
    laneWorkers: mutable.Map[Lane, ActorRef[LaneCollectorWorker.Protocol]])
    extends Agent[Protocol] {
  override protected val initialBehaviour: Behavior[Protocol] = idle

  lazy val idle: Behaviors.Receive[Protocol] = Behaviors.receiveMessagePartial {
    case TryCollect(replyTo, _, vehiclesAtLanes) =>
      val laneWorkersFiltered = for {
        (lane, drivers) <- vehiclesAtLanes
          .filterKeys(_.collectorPoint.isDefined)
        laneWorker = laneWorkers.getOrElseUpdate(
          lane,
          context.spawn(LaneCollectorWorker.init(lane, context.self),
                        s"lane-collector-${lane.id}"))
        _ = laneWorker ! LaneCollectorWorker.Protocol.TryCollect(drivers)
      } yield laneWorker

      getReadings(replyTo, laneWorkersFiltered.toSet, Set.empty)
  }

  def getReadings(
      replyTo: ActorRef[VehiclesCollectorStage.Protocol],
      awaiting: Set[ActorRef[LaneCollectorWorker.Protocol]],
      markedToDeletion: Set[ActorRef[AutonomousVehicleDriver.ExtendedProtocol]])
    : Behavior[Protocol] = {
    if (awaiting.isEmpty) {
      replyTo ! VehiclesCollectorStage.MarkedToDeletion(roadManagerRef,
                                                        markedToDeletion)
      idle
    } else {
      Behaviors.receiveMessagePartial {
        case LaneCollectResult(lane, laneMarkedForDeletion) =>
          getReadings(replyTo,
                      awaiting - lane,
                      markedToDeletion ++ laneMarkedForDeletion)
      }
    }
  }

}
object RoadCollectorWorker {
  def init(roadManagerRef: ActorRef[RoadManager.Protocol],
           lanes: List[Lane]): Behavior[Protocol] =
    Behaviors.setup { ctx =>
      val laneCollectors
        : mutable.Map[Lane, ActorRef[LaneCollectorWorker.Protocol]] =
        mutable.Map.empty

      for {
        lane <- lanes
        laneCollector = ctx.spawn(LaneCollectorWorker.init(lane, ctx.self),
                                  s"lane-collector-${lane.id}")
      } laneCollectors.update(lane, laneCollector)

      new RoadCollectorWorker(ctx, roadManagerRef, laneCollectors)
    }

  sealed trait Protocol extends SimulationProtocol
  case class TryCollect(
      replyTo: ActorRef[VehiclesCollectorStage.Protocol],
      entityManagerRef: ActorRef[EntityManager.Protocol],
      vehiclesAtLanes: Map[Lane,
                           Set[ActorRef[AutonomousVehicleDriver.Protocol]]])
      extends Protocol

  case class LaneCollectResult(
      lane: ActorRef[LaneCollectorWorker.Protocol],
      markedForDeletion: Set[ActorRef[AutonomousVehicleDriver.Protocol]])
      extends Protocol

}
