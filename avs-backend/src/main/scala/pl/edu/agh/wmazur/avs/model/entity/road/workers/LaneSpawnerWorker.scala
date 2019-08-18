package pl.edu.agh.wmazur.avs.model.entity.road.workers

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import org.locationtech.jts.geom.Geometry
import org.locationtech.spatial4j.shape.{Point, Shape}
import pl.edu.agh.wmazur.avs.Agent
import pl.edu.agh.wmazur.avs.model.entity.road.workers.LaneSpawnerWorker.{
  Context,
  PositionReading,
  Protocol,
  Spawned,
  Terminated,
  TrySpawn
}
import pl.edu.agh.wmazur.avs.model.entity.road.workers.RoadSpawnerWorker.NotSpawned
import pl.edu.agh.wmazur.avs.model.entity.road.{Lane, Road, SpawnPoint}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.Vehicle
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.Angle
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.{
  AutonomousVehicleDriver,
  VehicleDriver
}
import pl.edu.agh.wmazur.avs.protocol.SimulationProtocol
import pl.edu.agh.wmazur.avs.simulation.EntityManager
import pl.edu.agh.wmazur.avs.simulation.EntityManager.SpawnResult
import pl.edu.agh.wmazur.avs.model.entity.intersection.reservation.ReservationArray.Timestamp

class LaneSpawnerWorker(val context: ActorContext[Protocol],
                        spawnerContext: Context)
    extends Agent[Protocol] {
  val spawnPoint: Option[SpawnPoint] = spawnerContext.lane.spawnPoint
  var nextSpawnTime: Timestamp = 0L

  val spawnResultAdapter
    : ActorRef[SpawnResult[Vehicle, VehicleDriver.Protocol]] =
    context.messageAdapter[
      EntityManager.SpawnResult[Vehicle, VehicleDriver.Protocol]] {
      case SpawnResult(entity, ref) => Spawned(ref, entity.id)

    }

  override protected val initialBehaviour: Behavior[Protocol] = idle

  lazy val idle: Behaviors.Receive[Protocol] = Behaviors.receiveMessagePartial {
    case TrySpawn(drivers, entityManagerRef, currentTime) =>
      if (spawnPoint.isDefined && nextSpawnTime <= currentTime) {
        drivers.foreach { ref =>
          context.watchWith(ref, Terminated(ref))
          val adapter =
            context.messageAdapter[AutonomousVehicleDriver.BasicReading] {
              case AutonomousVehicleDriver.BasicReading(driverRef,
                                                        position,
                                                        heading,
                                                        _,
                                                        _,
                                                        area) =>
                PositionReading(driverRef, position, heading, area)
            }
          ref ! AutonomousVehicleDriver.GetPositionReading(adapter)
        }
        getReadings(entityManagerRef, drivers, Set.empty)
      } else {
        spawnerContext.roadSpawner ! NotSpawned(spawnerContext.lane)
        Behaviors.same
      }
  }

  private def getReadings(
      entityManagerRef: ActorRef[EntityManager.Protocol],
      awaiting: Set[ActorRef[AutonomousVehicleDriver.Protocol]],
      readings: Set[PositionReading]): Behavior[Protocol] = {

    if (awaiting.isEmpty) {
      trySpawn(entityManagerRef, readings)
    } else {
      Behaviors.receiveMessagePartial {
        case reading: PositionReading =>
          getReadings(entityManagerRef,
                      awaiting - reading.driverRef,
                      readings + reading)
        case Terminated(ref) =>
          getReadings(entityManagerRef, awaiting - ref, readings)
      }
    }
  }

  private def trySpawn(entityManagerRef: ActorRef[EntityManager.Protocol],
                       readings: Set[PositionReading]): Behavior[Protocol] = {
    val spawnPoint = spawnerContext.lane.spawnPoint.get
    val lane = spawnerContext.lane

    if (spawnPoint.canSpawn(readings)) {
      val spec = spawnPoint.getRandomSpec
      entityManagerRef ! EntityManager.SpawnProtocol
        .SpawnBasicVehicle(replyTo = Some(spawnResultAdapter),
                           spec = spec,
                           position = lane.entryPoint,
                           heading = lane.heading,
                           velocity = 10,
                           lane = lane,
                           None)
      waitForSpawnResult()
    } else {
      spawnerContext.roadSpawner ! RoadSpawnerWorker.NotSpawned(lane)
      idle
    }
  }

  def waitForSpawnResult(): Behavior[Protocol] =
    Behaviors.receiveMessagePartial {
      case LaneSpawnerWorker.Spawned(driverRef, id) =>
        spawnerContext.roadSpawner ! RoadSpawnerWorker
          .SpawnedAtLane(spawnerContext.lane, driverRef, id)
        nextSpawnTime += SpawnPoint.spawnInterval.toMillis
        idle
    }

}

object LaneSpawnerWorker {
  private case class Context(
      roadSpawner: ActorRef[RoadSpawnerWorker.Protocol],
      lane: Lane,
  )

  def init(replyTo: ActorRef[RoadSpawnerWorker.Protocol],
           lane: Lane): Behavior[Protocol] =
    Behaviors.setup[Protocol] { ctx =>
      val context = Context(roadSpawner = replyTo, lane = lane)
      new LaneSpawnerWorker(ctx, context)
    }

  sealed trait Protocol extends SimulationProtocol
  case class TrySpawn(drivers: Set[ActorRef[AutonomousVehicleDriver.Protocol]],
                      entityManagerRef: ActorRef[EntityManager.Protocol],
                      currentTime: Timestamp)
      extends Protocol
  case class PositionReading(
      driverRef: ActorRef[AutonomousVehicleDriver.Protocol],
      position: Point,
      heading: Angle,
      geometry: Geometry)
      extends Protocol
  case class Spawned(driverRef: ActorRef[AutonomousVehicleDriver.Protocol],
                     id: Vehicle#Id)
      extends Protocol
  case class Terminated(ref: ActorRef[AutonomousVehicleDriver.Protocol])
      extends Protocol
}
