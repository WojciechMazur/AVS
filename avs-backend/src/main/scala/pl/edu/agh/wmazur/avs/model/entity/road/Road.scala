package pl.edu.agh.wmazur.avs.model.entity.road

import com.softwaremill.quicklens._
import akka.actor.typed.ActorRef
import com.softwaremill.quicklens._
import org.locationtech.jts.geom.{Geometry, MultiPolygon}
import org.locationtech.spatial4j.shape._
import pl.edu.agh.wmazur.avs.model.entity.utils.SpatialUtils.{
  LineFactory,
  Point2
}
import pl.edu.agh.wmazur.avs.model.entity.utils.{
  DeltaOps,
  IdProvider,
  SpatialUtils
}
import pl.edu.agh.wmazur.avs.model.entity.{Entity, EntitySettings}

case class Road(id: Road#Id,
                lanes: List[Lane],
                managerRef: ActorRef[RoadManager.Protocol],
                oppositeRoad: Option[Road])
    extends Entity
    with DeltaOps[Road] {
  lanes.foreach(_.spec.road = Some(this))

  override lazy val area: Shape = {
    val shapeFactory = SpatialUtils.shapeFactory

    val geometry: Geometry = lanes
      .map(lane => shapeFactory.getGeometryFrom(lane.area))
      .reduce(_.union(_)) match {
      case multiPolygon: MultiPolygon =>
        multiPolygon.convexHull()
      case geo => geo
    }
    shapeFactory.makeShape(geometry)
  }
  override lazy val position: Point = area.getCenter

  override def isUpdatedBy(old: Road): Boolean = {
    lanes.size != old.lanes.size
  }

  override type Self = Road
  override def entitySettings: EntitySettings[Road] = Road
}

object Road extends EntitySettings[Road] with IdProvider[Road] {
  def apply(id: Option[Road#Id],
            lanes: List[Lane],
            managerRef: ActorRef[RoadManager.Protocol],
            oppositeRoad: Option[Road]): Road = {
    val lanesWithNeighbourhood = updateLanesNeighbourgood(lanes)

    new Road(
      id = id.getOrElse(nextId),
      lanes = lanesWithNeighbourhood,
      managerRef = managerRef,
      oppositeRoad = oppositeRoad
    )
  }

  def updateLanesNeighbourgood(lanes: List[Lane]): List[Lane] = {
    val lanesSorted =
      lanes.sortBy(lane => (lane.position.getX, lane.position.getY))
    //TODO check if lanes are neighbouring

    lanesSorted.tail
      .foldLeft(lanesSorted.head :: Nil) {
        case (acc, current: DirectedLane @unchecked) =>
          val left = acc.last
          left.spec.rightNeighbour = Some(current)
          val right = current
          right.spec.leftNeighbour = Some(left)
          acc.init ++ (left :: right :: Nil)
      }
  }

  def roadsWithSplitLanes(roads: Iterable[Road]): Iterable[Road] = {
    roads.map { road =>
      val intersectionArea = roads
        .filterNot(_.id == road.id)
        .filter(_.geometry.intersects(road.geometry))
        .map(_.geometry.intersection(road.geometry))
        .reduce(_ union _)
        .convexHull()

      val (lanesBeforeIntersection, lanesAfterIntersection) = road.lanes.map {
        lane =>
          val splitPoint = intersectionArea
            .intersection(lane.geometry)
            .getCentroid match {
            case p => Point2(p.getX, p.getY)
          }

          val beforeIntersectionLane =
            DirectedLane(id = lane.id,
                         middleLine = LineFactory(lane.entryPoint, splitPoint),
                         spec = lane.spec)

          val afterIntersectionLane =
            DirectedLane(id = Lane.nextId,
                         middleLine = LineFactory(splitPoint, lane.exitPoint),
                         spec = lane.spec)

          val before = beforeIntersectionLane
            .modify(_.spec)
            .using(_.leadsInto(afterIntersectionLane))
          val after = afterIntersectionLane
            .modify(_.spec)
            .using(_.leadsFrom(beforeIntersectionLane))
          (before, after)
      }.unzip

      val updatedLanes = Road.updateLanesNeighbourgood(lanesBeforeIntersection) ++
        Road.updateLanesNeighbourgood(lanesAfterIntersection)

      road.copy(lanes = updatedLanes)
    }
  }
}
