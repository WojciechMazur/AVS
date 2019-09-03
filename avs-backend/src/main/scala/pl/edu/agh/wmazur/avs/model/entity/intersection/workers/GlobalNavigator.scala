package pl.edu.agh.wmazur.avs.model.entity.intersection.workers

import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import org.locationtech.spatial4j.shape.Point
import pl.edu.agh.wmazur.avs.model.entity.intersection.workers.GlobalNavigator.Protocol.FindPath
import pl.edu.agh.wmazur.avs.model.entity.intersection.workers.GlobalNavigator.{
  IntersectionsListing,
  LanesConnection,
  RoadsListing,
  TrafficNetworkStateUpdate
}
import pl.edu.agh.wmazur.avs.model.entity.intersection.{
  Intersection,
  IntersectionManager
}
import pl.edu.agh.wmazur.avs.model.entity.road.{
  Lane,
  Road,
  RoadManager,
  TurningAllowance
}
import pl.edu.agh.wmazur.avs.model.entity.utils.MathUtils
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousVehicleDriver
import pl.edu.agh.wmazur.avs.protocol.SimulationProtocol
import pl.edu.agh.wmazur.avs.simulation.stage.SimulationStateGatherer
import pl.edu.agh.wmazur.avs.{Agent, Dimension, EntityRefsGroup}
import scalax.collection.GraphPredef._
import scalax.collection.edge.Implicits._
import scalax.collection.edge.LkDiEdge
import scalax.collection.mutable.Graph
import pl.edu.agh.wmazur.avs.model.entity.utils.SpatialUtils._

import scala.collection.mutable
import scala.util.Random

class GlobalNavigator(
    val context: ActorContext[GlobalNavigator.Protocol],
) extends Agent[GlobalNavigator.Protocol] {

  private var cachedRoadsRefs: Set[ActorRef[RoadManager.Protocol]] = Set.empty
  private var cachedIntersectionsRefs
    : Set[ActorRef[IntersectionManager.Protocol]] = Set.empty
  private val cachedLaneIdToRoad: mutable.Map[Lane#Id, Road] = mutable.Map.empty
  private val cachedLaneIdToLane: mutable.Map[Lane#Id, Lane] = mutable.Map.empty

  private val cachedPaths
    : mutable.Map[(Lane#Id, Road#Id), Option[List[List[Lane]]]] =
    mutable.Map.empty
  private val lanesNetwork = Graph.empty[Lane, LkDiEdge]

  private val stateReadingsAdapter =
    context.messageAdapter[SimulationStateGatherer.Protocol] {
      case SimulationStateGatherer
            .IntersectionDetailedReading(_, intersection) =>
        TrafficNetworkStateUpdate(Set.empty, Set(intersection))
      case SimulationStateGatherer.RoadDetailedReading(_, road) =>
        TrafficNetworkStateUpdate(Set(road), Set.empty)
    }

  override protected val initialBehaviour: Behavior[GlobalNavigator.Protocol] =
    Behaviors.receiveMessage {
      case FindPath(replyTo, currentLane, destination) =>
        val startNode: lanesNetwork.NodeT = currentLane match {
          case Left(laneId) => lanesNetwork.get(cachedLaneIdToLane(laneId))
          case Right(lane)  => lanesNetwork.get(lane)
        }

        def randomEndNode(): lanesNetwork.NodeT = {
          val nodes = lanesNetwork.nodes.filter(startNode.isConnectedWith)
          nodes.iterator
            .drop(Random.nextInt(nodes.size))
            .next()
        }

        val endNodes: List[lanesNetwork.NodeT] = destination match {
          case Some(Left(roadId)) =>
            lanesNetwork.nodes
              .filter(_.road.id == roadId) match {
              case s if s.isEmpty => randomEndNode() :: Nil
              case s              => s.toList
            }
          case Some(Right(road)) => road.lanes.map(lanesNetwork.get)
          case None              => randomEndNode() :: Nil
        }

        def calcPaths: Option[List[List[Lane]]] =
          endNodes
            .map(startNode.pathTo)
            .flatten
            .map(_.nodes.map(_.toOuter).toList) match {
            case Nil   => None
            case paths => Some(paths)
          }

        cachedPaths.getOrElseUpdate((startNode.id, endNodes.head.road.id),
                                    calcPaths) match {
          case Some(shortestsPath :: _) =>
            val roads = shortestsPath.map(_.road)
            replyTo ! AutonomousVehicleDriver.PathToFollow(roads, shortestsPath)
          case other =>
            replyTo ! AutonomousVehicleDriver.NoPathFound
        }
        Behaviors.same

      case TrafficNetworkStateUpdate(roads, intersections) =>
        roads
          .flatMap(_.lanes)
          .foreach { lane =>
            if (!lanesNetwork.contains(lane)) {
              val id = lane.id
              lanesNetwork.add(lane)
              cachedLaneIdToRoad.update(id, lane.road)
              cachedLaneIdToLane.update(id, lane)
            }
          }

        if (intersections.nonEmpty) {
          cachedPaths.clear()
        }

        intersections.foreach { intersection =>
          for {
            (entryLane, entryHeading) <- intersection.entryHeadings
            (departureLane, departureHeading) <- intersection.exitHeading
            allowance = entryLane.spec.turningAllowance
            headingDelta = MathUtils.boundedAngle(
              departureHeading - entryHeading)
          } {
            val lanesConnection = (entryLane ~+#> departureLane)(
              LanesConnection(intersection.entryPoints(entryLane),
                              intersection.exitPoints(departureLane)))
            //TODO możliwość precyjnego wskazania na które wyjściowe jezdnie może wjechać. Teoretycznie podczas planowania i tak powinna zostać wybrana najkrótsza.
            if (allowance.canGoStraight(headingDelta)) {
              lanesNetwork += lanesConnection
            }

            if (allowance.canTurnRight(headingDelta)) {
              lanesNetwork += lanesConnection
            }

            if (allowance.canTurnLeft(headingDelta)) {
              lanesNetwork += lanesConnection
            }
          }
        }

        Behaviors.same

      case RoadsListing(roadsRefs) =>
        val deleted = cachedRoadsRefs.diff(roadsRefs)
        val added = roadsRefs.diff(cachedRoadsRefs)

        for {
          ref <- deleted
          node <- lanesNetwork.nodes.find(_.road.managerRef == ref)
        } {
          lanesNetwork.remove(node)
        }

        added.foreach(_ ! RoadManager.GetDetailedReadings(stateReadingsAdapter))
        cachedRoadsRefs = roadsRefs
        Behaviors.same

      case IntersectionsListing(intersectionsRefs) =>
        val added = intersectionsRefs.diff(cachedIntersectionsRefs)
        added.foreach(
          _ ! IntersectionManager.GetDetailedReadings(stateReadingsAdapter))
        cachedIntersectionsRefs = intersectionsRefs
        Behaviors.same
    }
}

object GlobalNavigator {
  case class LanesConnection(from: Point, to: Point) {
    override def equals(obj: Any): Boolean =
      obj match {
        case that: LanesConnection =>
          this.from.distance(that.from) <= 0.01.meters &&
            this.to.distance(that.to) <= 0.01.meters
        case _ => false

      }
  }

  def init: Behavior[Protocol] = Behaviors.setup { ctx =>
    val listingAdapter = ctx.messageAdapter[Receptionist.Listing] {
      case EntityRefsGroup.intersection.Listing(listing) =>
        IntersectionsListing(listing)
      case EntityRefsGroup.road.Listing(listing) => RoadsListing(listing)
    }

    ctx.system.receptionist ! Receptionist.subscribe(EntityRefsGroup.road,
                                                     listingAdapter)
    ctx.system.receptionist ! Receptionist
      .subscribe(EntityRefsGroup.intersection, listingAdapter)

    new GlobalNavigator(ctx)
  }

  sealed trait Protocol extends SimulationProtocol

  case class TrafficNetworkStateUpdate(roads: Set[Road],
                                       intersections: Set[Intersection])
      extends Protocol

  case class RoadsListing(roadsRefs: Set[ActorRef[RoadManager.Protocol]])
      extends Protocol
  case class IntersectionsListing(
      intersectionsRefs: Set[ActorRef[IntersectionManager.Protocol]])
      extends Protocol

  object Protocol {
    case class FindPath(
        replyTo: ActorRef[AutonomousVehicleDriver.Protocol],
        currentLane: Either[Lane#Id, Lane],
        destinationRoadId: Option[Either[Road#Id, Road]] = None,
    ) extends Protocol

  }
}
