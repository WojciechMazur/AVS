package pl.edu.agh.wmazur.avs.model.entity.road

import com.softwaremill.quicklens._
import org.locationtech.jts.geom.Geometry
import org.locationtech.spatial4j.shape._
import pl.edu.agh.wmazur.avs.model.entity.utils.{
  DeltaOps,
  IdProvider,
  SpatialUtils
}
import pl.edu.agh.wmazur.avs.model.entity.{Entity, EntitySettings}

case class Road(id: Road#Id, lanes: List[Lane], var oppositeRoad: Option[Road])
    extends Entity
    with DeltaOps[Road] {

  override lazy val area: Shape = {
    val shapeFactory = SpatialUtils.shapeFactory
    val geometry: Geometry = lanes
      .map(lane => shapeFactory.getGeometryFrom(lane.area))
      .reduce(_.union(_))
    shapeFactory.makeShape(geometry)

  }
  override lazy val position: Point = area.getCenter

  override def isUpdatedBy(old: Road): Boolean = false

  override type Self = Road
  override def entitySettings: EntitySettings[Road] = Road
}

object Road extends EntitySettings[Road] with IdProvider[Road] {
  def apply(lanes: List[Lane], oppositeRoad: Option[Road] = None): Road = {
    val x = lanes.tail
      .foldLeft(lanes.head :: Nil) {
        case (acc, current: DirectedLane) =>
          lazy val left: Option[DirectedLane] = acc.last match {
            case lane: DirectedLane =>
              Some(
                lane
                  .modify(_.spec)
                  .using(_.copy(rightNeighbourLane = right))
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
//      .map {
//        case List(left: DirectedLane, right, _*) =>
//          left.modify(_.spec.rightNeighbourLane).setTo(Some(right))
//        case left :: Nil => left
//      }
//    val y = x
//      .sliding(2, 1)
//      .map {
//        case List(left, right: DirectedLane, _*) =>
//          right.modify(_.spec.leftNeighbourLane).setTo(Some(left))
//        case left :: Nil => left
//      }
//      .toList
    val lanesWithNeighbourhood = x

    val self = new Road(
      id = nextId,
      lanes = lanesWithNeighbourhood,
      oppositeRoad = oppositeRoad
    )
    lanes.foreach(_.spec.road = Some(self))
    oppositeRoad.foreach(_.oppositeRoad = Some(self))
    self
  }
}
