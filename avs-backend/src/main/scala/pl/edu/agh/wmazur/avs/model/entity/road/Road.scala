package pl.edu.agh.wmazur.avs.model.entity.road

import akka.actor.typed.ActorRef
import com.softwaremill.quicklens._
import org.locationtech.jts.geom.{Geometry, MultiPolygon}
import org.locationtech.spatial4j.shape._
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
    val lanesWithNeighbourhood = lanes.tail
      .foldLeft(lanes.head :: Nil) {
        case (acc, current: DirectedLane @unchecked) =>
          lazy val left: Option[DirectedLane] = acc.last match {
            case lane: DirectedLane =>
              Some(
                lane
                  .modify(_.spec)
                  .using(_.copy(rightNeighbourLane = right))
                //using because of lazy val evaluation
              )
            case _ => throw new RuntimeException("Unknown type of lane")
          }
          lazy val right: Option[DirectedLane] = Some(
            current
              .modify(_.spec)
              .using(_.copy(leftNeighbourLane = left))
          )
          acc.init ++ (left :: right :: Nil).flatten
      }

    new Road(
      id = id.getOrElse(nextId),
      lanes = lanesWithNeighbourhood,
      managerRef = managerRef,
      oppositeRoad = oppositeRoad
    )
  }
}
