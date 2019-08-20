package pl.edu.agh.wmazur.avs.model.entity.intersection.workers

import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
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
import pl.edu.agh.wmazur.avs.model.entity.road.{Lane, Road, RoadManager}
import pl.edu.agh.wmazur.avs.model.entity.utils.MathUtils
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousVehicleDriver
import pl.edu.agh.wmazur.avs.protocol.SimulationProtocol
import pl.edu.agh.wmazur.avs.simulation.stage.SimulationStateGatherer
import pl.edu.agh.wmazur.avs.{Agent, EntityRefsGroup}
import scalax.collection.GraphPredef
import scalax.collection.edge.Implicits._
import scalax.collection.edge.LkDiEdge
import scalax.collection.mutable.Graph

import scala.collection.mutable
import scala.util.Random

class GlobalNavigator(
    val context: ActorContext[GlobalNavigator.Protocol],
) extends Agent[GlobalNavigator.Protocol] {

  private var cachedRoadsRefs: Set[ActorRef[RoadManager.Protocol]] = Set.empty
  private var cachedIntersectionsRefs
    : Set[ActorRef[IntersectionManager.Protocol]] = Set.empty
  private var cachedLaneIdToRoad: mutable.Map[Lane#Id, Road] = mutable.Map.empty

  private val roadsNetwork = Graph.empty[Road, LkDiEdge]
//  private val intersectionNetwork = mutable.Graph.empty[Intersection, LkDiEdge]

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
        def randomEndNode(): roadsNetwork.NodeT =
          roadsNetwork.nodes.iterator
            .drop(Random.nextInt(roadsNetwork.size))
            .next()

        val startNode: roadsNetwork.NodeT = currentLane match {
          case Left(laneId) => roadsNetwork.get(cachedLaneIdToRoad(laneId))
          case Right(lane)  => roadsNetwork.get(lane.road)
        }

        val endNode: roadsNetwork.NodeT = destination match {
          case Some(Left(roadId)) =>
            roadsNetwork.nodes.find(_.id == roadId).getOrElse(randomEndNode())
          case Some(Right(road)) => roadsNetwork.get(road)
          case None              => randomEndNode()
        }

        startNode shortestPathTo endNode match {
          case Some(path) =>
            val roads = path.nodes.map(_.toOuter).toList
//            val pathAsList = path.edges
//              .map(_.toOuter.label)
//              .collect {
//                case lc: LanesConnection => lc.asInstanceOf[LanesConnection]
//              }
//              .foldLeft(List.empty[Lane]) {
//                case (acc, LanesConnection(from, to)) if acc.isEmpty =>
//                  from :: to :: Nil
//                case (acc, LanesConnection(from, to)) =>
//                  assert(acc.last.id == from.id,
//                         s"last ${acc.last.id} did not equal ${from.id}")
//                  acc :+ to
//              }
            replyTo ! AutonomousVehicleDriver.PathToFollow(roads, Nil)
          case None => replyTo ! AutonomousVehicleDriver.NoPathFound
        }

        Behaviors.same

      case TrafficNetworkStateUpdate(roads, intersections) =>
        roads.foreach { road =>
          if (!roadsNetwork.contains(road)) {
            roadsNetwork.add(road)
            road.lanes
              .map(_.id)
              .foreach(cachedLaneIdToRoad.update(_, road))
          }
        }

        intersections.foreach { intersection =>
          for {
            (entryLane, entryHeading) <- intersection.entryHeadings
            (departureLane, departureHeading) <- intersection.exitHeading
            entryRoad = entryLane.road
            departureRoad = departureLane.road
            allowance = entryLane.spec.turningAllowance
            label = LanesConnection(entryLane, departureLane)
            roadsConnection = (entryRoad ~+#> departureRoad)(label)
            headingDelta = MathUtils.boundedAngle(
              departureHeading - entryHeading)
          } {
            //TODO możliwość precyjnego wskazania na które wyjściowe jezdnie może wjechać. Teoretycznie podczas planowania i tak powinna zostać wybrana najkrótsza.
            if (allowance.canGoStraight(headingDelta))
              roadsNetwork += roadsConnection

            if (allowance.canTurnRight(headingDelta))
              roadsNetwork += roadsConnection

            if (allowance.canTurnLeft(headingDelta))
              roadsNetwork += roadsConnection
          }
        }

        Behaviors.same

      case RoadsListing(roadsRefs) =>
        val deleted = cachedRoadsRefs.diff(roadsRefs)
        val added = roadsRefs.diff(cachedRoadsRefs)

        for {
          ref <- deleted
          node <- roadsNetwork.nodes.find(_.managerRef == ref)
        } {
          roadsNetwork.remove(node)
        }

        added.foreach(_ ! RoadManager.GetDetailedReadings(stateReadingsAdapter))
        cachedRoadsRefs = roadsRefs
        Behaviors.same

      case IntersectionsListing(intersectionsRefs) =>
        val added = intersectionsRefs.diff(cachedIntersectionsRefs)
        //TODO Intersections network
        //val deleted = cachedIntersectionsRefs.diff(intersectionsRefs)
        //for {
        //  ref <- deleted
        //  node <- roadsNetwork.nodes.find(_.managerRef == ref)
        //} {
        //  roadsNetwork.remove(node)
        //}
        added.foreach(
          _ ! IntersectionManager.GetDetailedReadings(stateReadingsAdapter))
        cachedIntersectionsRefs = intersectionsRefs
        Behaviors.same
    }
}

object GlobalNavigator {
  case class LanesConnection(from: Lane, to: Lane) {
    def fromId: from.Id = from.id
    def toId: to.Id = to.id

    override def equals(obj: Any): Boolean =
      obj match {
        case that: LanesConnection =>
          this.fromId == that.fromId && this.toId == that.toId
        case _ => false

      }

    override def toString: String = s"($fromId~>$toId)"
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
