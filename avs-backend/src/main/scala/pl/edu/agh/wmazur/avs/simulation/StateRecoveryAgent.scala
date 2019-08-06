package pl.edu.agh.wmazur.avs.simulation

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import org.locationtech.spatial4j.shape.SpatialRelation
import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.Entity
import pl.edu.agh.wmazur.avs.model.entity.intersection.{
  Intersection,
  IntersectionManager
}
import pl.edu.agh.wmazur.avs.model.entity.road.{
  DirectedLane,
  LaneSpec,
  Road,
  RoadManager
}
import pl.edu.agh.wmazur.avs.protocol.SimulationProtocol
import pl.edu.agh.wmazur.avs.simulation.EntityManager.SpawnResult
object StateRecoveryAgent {

  sealed trait Protocol extends SimulationProtocol

  case class StartRecovery(
      simManagerRef: ActorRef[SimulationManager.Protocol.RecoveryResult])
      extends Protocol

  case class RoadSpawned(road: Road, manager: ActorRef[RoadManager.Protocol])
      extends Protocol
  case class IntersectionSpawned(
      intersection: Intersection,
      manager: ActorRef[IntersectionManager.Protocol])
      extends Protocol

  private case class Adapters(
      roadsAdapter: ActorRef[
        EntityManager.SpawnResult[Road, RoadManager.Protocol]],
      intersectionAdapter: ActorRef[
        EntityManager.SpawnResult[Intersection, IntersectionManager.Protocol]]
  )

  private case class Context(
      actorContext: ActorContext[Protocol],
      simulationManager: ActorRef[SimulationManager.Protocol.RecoveryResult],
      entityManager: ActorRef[EntityManager.Protocol],
      adapter: ActorRef[EntityManager.SpawnResult[_ <: Entity, _]]
  )

  def init(
      entityManagerRef: ActorRef[EntityManager.Protocol]): Behavior[Protocol] =
    Behaviors.receive {
      case (ctx, StartRecovery(simManagerRef)) =>
        ctx.log.info("Starting recovery")

        val adapter = ctx.messageAdapter[SpawnResult[_ <: Entity, _]] {
          case SpawnResult(entity, ref) =>
            (entity, ref) match {
              case (road: Road,
                    ref: ActorRef[RoadManager.Protocol] @unchecked) =>
                RoadSpawned(road, ref)
              case (intersection: Intersection,
                    ref: ActorRef[IntersectionManager.Protocol] @unchecked) =>
                IntersectionSpawned(intersection, ref)
            }
        }

        val context = Context(ctx, simManagerRef, entityManagerRef, adapter)
        spawnRoads(context)
    }

  def spawnRoads(context: Context): Behavior[Protocol] = {
    val laneSpec = new LaneSpec(30, 2.5)

    val lane11 =
      DirectedLane.simple(spec = laneSpec,
                          offStartX = -250.0.fromMeters,
                          length = 500.0.fromMeters)

    val lane12 = DirectedLane.simple(
      spec = laneSpec.copy(speedLimit = 10),
      offStartX = -250.0.fromMeters,
      offStartY = laneSpec.width + 0.5.fromMeters,
      length = 500.0.fromMeters)

    val lane13 = DirectedLane.simple(
      spec = laneSpec.copy(speedLimit = 10),
      offStartX = -250.0.fromMeters,
      offStartY = 2 * laneSpec.width + 2 * 0.5.fromMeters,
      length = 500.0.fromMeters,
    )

    val lane21 = DirectedLane.simple(spec = laneSpec,
                                     offStartY = -100.0.fromMeters,
                                     length = 200.0.fromMeters,
                                     heading = Math.PI / 2)

    val lane22 = DirectedLane.simple(spec = laneSpec,
                                     offStartY = -100d,
                                     offStartX = laneSpec.width + 0.5,
                                     length = 250.0,
                                     heading = Math.PI / 2)

    val lane23 = DirectedLane.simple(spec = laneSpec,
                                     offStartY = -100d,
                                     offStartX = 2 * laneSpec.width + 2 * 0.5,
                                     length = 300.0,
                                     heading = Math.PI / 2)

    val roadLanes1 = lane11 :: lane12 :: lane13 :: Nil
    val roadLanes2 = lane21 :: lane22 :: lane23 :: Nil

    val roadsToSpawn = List(roadLanes1, roadLanes2)
    roadsToSpawn.foreach { roadLanes =>
      context.entityManager ! EntityManager.SpawnProtocol
        .SpawnRoad(Some(context.adapter), roadLanes)
    }

    waitForRoadsSpawn(context, roadsToSpawn.size)
  }

  def waitForRoadsSpawn(context: Context,
                        awaiting: Int,
                        roads: Map[Road, ActorRef[RoadManager.Protocol]] =
                          Map.empty): Behavior[Protocol] =
    if (awaiting == 0) {
      spawnIntersections(context, roads)
    } else {
      Behaviors.receiveMessage {
        case RoadSpawned(road, ref) =>
          waitForRoadsSpawn(context, awaiting - 1, roads + (road -> ref))
      }
    }

  def spawnIntersections(
      context: StateRecoveryAgent.Context,
      roads: Map[Road, ActorRef[RoadManager.Protocol]]): Behavior[Protocol] = {

    val roadsSet = roads.keySet
    val intersectiongRoads = for {
      road <- roadsSet
      intersections = (roadsSet - road).filter(
        _.area.relate(road.area) == SpatialRelation.INTERSECTS)
    } yield intersections + road
    val distinct = intersectiongRoads.foldLeft(Set.empty[Set[Road]]) {
      case (acc, set) =>
        if (acc.exists(_.diff(set).isEmpty)) {
          acc
        } else {
          acc + set
        }
    }

    distinct.foreach { intersectionRoads =>
      context.entityManager ! EntityManager.SpawnProtocol
        .SpawnAutonomousIntersection(Some(context.adapter),
                                     intersectionRoads.toList)
    }

    waitForIntersectionsSpawn(context, roads, Map.empty, distinct.size)
  }

  def waitForIntersectionsSpawn(
      context: Context,
      roads: Map[Road, ActorRef[RoadManager.Protocol]],
      intersections: Map[Intersection, ActorRef[IntersectionManager.Protocol]],
      awaiting: Int): Behavior[Protocol] =
    if (awaiting == 0) {
      context.simulationManager ! SimulationManager.Protocol.RecoveryResult
        .RecoveryFinished(roads, intersections)
      Behaviors.stopped
    } else {
      Behaviors.receiveMessage {
        case IntersectionSpawned(entity, ref) =>
          waitForIntersectionsSpawn(context,
                                    roads,
                                    intersections + (entity -> ref),
                                    awaiting - 1)
      }
    }
}
