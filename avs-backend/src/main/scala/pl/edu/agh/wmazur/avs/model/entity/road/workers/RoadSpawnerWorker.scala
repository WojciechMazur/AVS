package pl.edu.agh.wmazur.avs.model.entity.road.workers

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import pl.edu.agh.wmazur.avs.model.entity.road.{Lane, RoadManager}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.Vehicle
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousVehicleDriver
import pl.edu.agh.wmazur.avs.simulation.EntityManager
import pl.edu.agh.wmazur.avs.model.entity.intersection.reservation.ReservationArray.Timestamp
import pl.edu.agh.wmazur.avs.simulation.stage.VehiclesSpawnerStage
import pl.edu.agh.wmazur.avs.simulation.stage.VehiclesSpawnerStage.RoadSpawnResult

object RoadSpawnerWorker {
  private case class Context(
      self: ActorRef[Protocol],
      roadRef: ActorRef[RoadManager.Protocol],
      laneSpawners: Map[Lane, ActorRef[LaneSpawnerWorker.Protocol]],
      mainSpawnerRef: Option[ActorRef[VehiclesSpawnerStage.Protocol]] = None,
      entityManagerRef: Option[ActorRef[EntityManager.Protocol]] = None)

  def init(roadRef: ActorRef[RoadManager.Protocol],
           lanes: List[Lane]): Behavior[Protocol] =
    Behaviors.setup[Protocol] { ctx =>
      val laneSpawners: Map[Lane, ActorRef[LaneSpawnerWorker.Protocol]] =
        lanes.map { lane =>
          lane -> ctx.spawn(LaneSpawnerWorker.init(ctx.self, lane),
                            s"lane-spawner-${lane.id}")
        }(scala.collection.breakOut)

      val context = Context(
        self = ctx.self,
        roadRef = roadRef,
        laneSpawners = laneSpawners
      )
      idle(context)
    }

  def idle(context: Context): Behavior[Protocol] =
    Behaviors.receiveMessagePartial {
      case TrySpawn(replyTo: ActorRef[VehiclesSpawnerStage.Protocol],
                    entityManagerRef,
                    vehiclesAtLanes,
                    currentTime) =>
        val filteredLanes =
          vehiclesAtLanes.filterKeys(context.laneSpawners.contains)
        filteredLanes
          .foreach {
            case (lane, drivers) =>
              context.laneSpawners(lane) ! LaneSpawnerWorker.TrySpawn(
                drivers,
                entityManagerRef,
                currentTime)
          }
        val enrichedContext = context.copy(mainSpawnerRef = Some(replyTo),
                                           entityManagerRef =
                                             Some(entityManagerRef))
        waitForLaneSpawners(enrichedContext, filteredLanes.keySet, Map.empty)

    }

  private def waitForLaneSpawners(
      context: Context,
      awaiting: Set[Lane],
      spawned: Map[ActorRef[AutonomousVehicleDriver.ExtendedProtocol],
                   Vehicle#Id]): Behavior[Protocol] = {
    if (awaiting.isEmpty) {
      context.mainSpawnerRef.get ! RoadSpawnResult(context.roadRef, spawned)
      idle(context)
    } else {
      Behaviors.receiveMessagePartial {
        case SpawnedAtLane(lane, driverRef, vehicle) =>
          waitForLaneSpawners(context,
                              awaiting - lane,
                              spawned + (driverRef -> vehicle))
        case NotSpawned(lane) =>
          waitForLaneSpawners(context, awaiting - lane, spawned)
      }
    }
  }

  // format: off
  sealed trait Protocol
  case class TrySpawn(
      replyTo: ActorRef[VehiclesSpawnerStage.Protocol],
      entityManagerRef: ActorRef[EntityManager.Protocol],
      vehiclesAtLanes: Map[Lane,Set[ActorRef[AutonomousVehicleDriver.Protocol]]],
      currentTime: Timestamp)
      extends Protocol

  sealed trait LaneSpawnResult extends Protocol
  case class SpawnedAtLane(
      lane: Lane,
      driverRef: ActorRef[AutonomousVehicleDriver.ExtendedProtocol],
      vehicleId: Vehicle#Id)
      extends LaneSpawnResult
  case class NotSpawned(lane: Lane) extends LaneSpawnResult
  // format: on
}
