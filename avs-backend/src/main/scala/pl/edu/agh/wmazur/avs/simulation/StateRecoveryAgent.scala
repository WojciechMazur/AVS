package pl.edu.agh.wmazur.avs.simulation
import pl.edu.agh.wmazur.avs.model.entity.utils.SpatialUtils._
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
    Behaviors.receivePartial {
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
    val laneSpec = new LaneSpec(16.6, 2.5, canSpawn = true)
    import pl.edu.agh.wmazur.avs.model.entity.road.TurningAllowance._
    val lane11 =
      DirectedLane.simple(spec = laneSpec.withTurningAllowance(TurnLeftOnly),
                          offStartX = -125.0.meters,
                          offStartY = -(laneSpec.width + 0.5.meters) - 2.meters,
                          length = 300.0.meters)

    val lane12 = DirectedLane.simple(
      spec = laneSpec.withTurningAllowance(GoStraightOnly),
      offStartX = -125.0.meters,
      offStartY = 2 * -(laneSpec.width + 0.5.meters) - 2.meters,
      length = 300.0.meters)

    val lane13 = DirectedLane.simple(
      spec = laneSpec.withTurningAllowance(TurnRightOnly),
      offStartX = -125.0.meters,
      offStartY = 3 * -(laneSpec.width + 0.5.meters) - 2.meters,
      length = 300.0.meters,
    )

    val lane21 = DirectedLane.simple(
      spec = laneSpec.withTurningAllowance(TurnLeftOnly),
      offStartY = -125.0.meters,
      length = 300.0.meters,
      heading = Math.PI / 2)

    val lane22 = DirectedLane.simple(
      spec = laneSpec.withTurningAllowance(GoStraightOnly),
      offStartY = -125d,
      offStartX = laneSpec.width + 0.5,
      length = 350.0,
      heading = Math.PI / 2)

    val lane23 = DirectedLane.simple(
      spec = laneSpec.withTurningAllowance(TurnRightOnly),
      offStartY = -125d,
      offStartX = 2 * laneSpec.width + 2 * 0.5,
      length = 400.0,
      heading = Math.PI / 2)

    val lane31 =
      DirectedLane.simple(spec = laneSpec.withTurningAllowance(TurnLeftOnly),
                          offStartX = 150.0.meters,
                          length = 300.0.meters,
                          heading = Math.PI)

    val lane32 = DirectedLane.simple(
      spec = laneSpec.withTurningAllowance(GoStraightOnly),
      offStartX = 150.0.meters,
      offStartY = laneSpec.width + 0.5.meters,
      length = 300.0.meters,
      heading = Math.PI)

    val lane33 = DirectedLane.simple(
      spec = laneSpec.withTurningAllowance(TurnRightOnly),
      offStartX = 150.0.meters,
      offStartY = 2 * (laneSpec.width + 0.5.meters),
      length = 300.0.meters,
      heading = Math.PI)

    val lane41 = DirectedLane.simple(
      spec = laneSpec.withTurningAllowance(TurnLeftOnly),
      offStartY = 150.0.meters,
      offStartX = -(laneSpec.width + 0.5.meters) - 1.meters,
      length = 300.0.meters,
      heading = -Math.PI / 2
    )

    val lane42 = DirectedLane.simple(
      spec = laneSpec.withTurningAllowance(GoStraightOnly),
      offStartY = 150d,
      offStartX = 2 * -(laneSpec.width + 0.5.meters) - 1.meters,
      length = 300.0,
      heading = -Math.PI / 2)

    val lane43 = DirectedLane.simple(
      spec = laneSpec.withTurningAllowance(TurnRightOnly),
      offStartY = 150d,
      offStartX = 3 * -(laneSpec.width + 0.5.meters) - 1.meters,
      length = 300.0,
      heading = -Math.PI / 2)

    val roadLanes1 = lane11 :: Nil // lane12 :: lane13 :: Nil
    val roadLanes2 = lane21 :: Nil //lane22 :: lane23 :: Nil
    val roadLanes3 = lane31 :: Nil //lane32 :: lane33 :: Nil
    val roadLanes4 = lane41 :: Nil //lane42 :: lane43 :: Nil

    val roadsToSpawn = List(roadLanes1, roadLanes2, roadLanes3, roadLanes4)
    val roadGroups = List((roadLanes1, roadLanes3), (roadLanes2, roadLanes4))

    roadGroups.foreach {
      case (roadLanes, oppositeRoadLanes) =>
        context.entityManager ! EntityManager.SpawnProtocol
          .SpawnOppositeRoads(Some(context.adapter),
                              roadLanes,
                              oppositeRoadLanes)
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
      Behaviors.receiveMessagePartial {
        case RoadSpawned(road, ref) =>
          waitForRoadsSpawn(context, awaiting - 1, roads + (road -> ref))
      }
    }

  def spawnIntersections(
      context: StateRecoveryAgent.Context,
      roads: Map[Road, ActorRef[RoadManager.Protocol]]): Behavior[Protocol] = {

    def roadsIntersect(lRoad: Road, rRoad: Road): Boolean = {
      val case1 = lRoad.area.relate(rRoad.area).intersects()
      def case2 =
        lRoad.oppositeRoad.exists(_.area.relate(rRoad.area).intersects())
      def case3 =
        rRoad.oppositeRoad.exists(_.area.relate(rRoad.area).intersects())
      def case4 =
        rRoad.oppositeRoad.exists(_.area.relate(lRoad.area).intersects())

      case1 || case2 || case3 || case4
    }

    val roadsSet = roads.keySet
    val intersectiongRoads = for {
      road <- roadsSet
      intersections = (roadsSet - road).filter(roadsIntersect(_, road))
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
      val sortedRoads =
        intersectionRoads.toList.sortBy(_.position.angle(Point2(0, 0)))
      context.entityManager ! EntityManager.SpawnProtocol
        .SpawnAutonomousIntersection(Some(context.adapter), sortedRoads)
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
      Behaviors.receiveMessagePartial {
        case IntersectionSpawned(entity, ref) =>
          waitForIntersectionsSpawn(context,
                                    roads,
                                    intersections + (entity -> ref),
                                    awaiting - 1)
      }
    }
}
