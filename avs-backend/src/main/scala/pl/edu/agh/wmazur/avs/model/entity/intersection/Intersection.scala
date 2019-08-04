package pl.edu.agh.wmazur.avs.model.entity.intersection

import mikera.vectorz.Vector2
import org.locationtech.spatial4j.shape.Point
import pl.edu.agh.wmazur.avs.Dimension
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

  def entryPoints: Map[Lane, Point]
  def exitPoints: Map[Lane, Point]
  def entryHeadings: Map[Lane, Angle]
  def exitHeading: Map[Lane, Angle]

  def isExitedBy(lane: Lane): Boolean = exitPoints.contains(lane)
  def isEnteredBy(lane: Lane): Boolean = entryPoints.contains(lane)

  def calcTravelsalDistance(arrivalLane: Lane, departureLane: Lane): Dimension

  lazy val lanesById: Map[LaneId, Lane] = (entryRoads ++ exitRoads).flatMap {
    road =>
      road.lanes.map { lane =>
        lane.id -> lane
      }
  }.toMap

  override def isUpdatedBy(old: Intersection): Boolean = false
}

object Intersection extends IdProvider[Intersection]
