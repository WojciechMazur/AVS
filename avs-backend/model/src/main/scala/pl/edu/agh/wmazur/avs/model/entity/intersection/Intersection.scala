package pl.edu.agh.wmazur.avs.model.entity.intersection

import mikera.vectorz.Vector2
import org.locationtech.spatial4j.shape.Point
import pl.edu.agh.wmazur.avs.model.entity.Entity
import pl.edu.agh.wmazur.avs.model.entity.road.{Lane, Road}
import pl.edu.agh.wmazur.avs.model.entity.utils.DeltaOps
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.Angle
import pl.edu.agh.wmazur.avs.model.entity.utils._

import scala.collection.SortedMap

trait Intersection extends Entity with DeltaOps[Intersection] {
  import MathUtils._
  import SpatialUtils._

  type LaneId = Lane#Id
  def inletsLimit: Int = 4
  def outletsLimit: Int = inletsLimit

  def entryRoads: Iterable[Road]
  def exitRoads: Iterable[Road]
//  lazy val roads: Iterable[Road] = inletRoads ++ outletRoads
  def roads: Iterable[Road]

  lazy val centerPoint: Point = this.area.getCenter
  lazy val centerPointVec2: Vector2 = centerPoint.vector

  val entryPoints: Map[Lane, Point] = entryRoads
    .flatMap(_.lanes)
    .map(lane => lane -> lane.exitPoint)
    .toMap

  val exitPoints: Map[Lane, Point] = exitRoads
    .flatMap(_.lanes)
    .map(lane => lane -> lane.entryPoint)
    .toMap

  val entryHeadings: Map[Lane, Angle] =
    entryPoints.mapValues(_.angle(centerPointVec2))
  val exitHeading: Map[Lane, Angle] =
    exitPoints.mapValues(_.angle(centerPointVec2))

  val lanesById: Map[LaneId, Lane] = (entryRoads ++ exitRoads).flatMap { road =>
    road.lanes.map { lane =>
      lane.id -> lane
    }
  }.toMap

  def intersectionManager: IntersectionManager

  override def isUpdatedBy(old: Intersection): Boolean = false
}
