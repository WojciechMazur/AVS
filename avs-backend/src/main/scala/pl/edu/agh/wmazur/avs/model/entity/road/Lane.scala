package pl.edu.agh.wmazur.avs.model.entity.road

import org.locationtech.jts.geom.{Geometry, LineString}
import org.locationtech.spatial4j.shape.{Point, Shape}
import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.utils.IdProvider
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.Angle
import pl.edu.agh.wmazur.avs.model.entity.{Entity, Identifiable}

trait Lane extends Entity with Identifiable {
  def spec: LaneSpec
  def road: Road = spec.road.get

  def length: Dimension
  def heading: Angle

  def entryPoint: Point
  def exitPoint: Point

  def rightBorder: LineString
  def leftBorder: LineString
  def middleLine: LineString

  def leadPointOf(point: Point, distance: Dimension): Point
  def distanceFromPoint(pos: Point): Dimension
  def remainingDistanceAlongLane(pos: Point): Dimension =
    length - distanceAlongLane(pos)

  def leftIntersectionPoint(line: LineString): Option[Point]
  def rightIntersectionPoint(line: LineString): Option[Point]
  def centerIntersectionPoint(line: LineString): Option[Point]

  def intersectionPoints(line: LineString): List[Point] = {
    (
      leftIntersectionPoint(line) ::
        centerIntersectionPoint(line) ::
        rightIntersectionPoint(line) ::
        Nil
    ).flatten
  }

  def collectorPoint: Option[CollectorPoint]
  def spawnPoint: Option[SpawnPoint]

  def getGeometryFraction(start: Double, end: Double): Geometry
  def pointAtNormalizedDistance(normalizedDistance: Double): Point
  def headingAtNormalizedDistance(normalizedDistance: Double): Angle
  def distanceAlongLane(point: Point): Dimension
  def normalizedDistanceAlongLane(point: Point): Double

  override def equals(obj: Any): Boolean = obj match {
    case lane: Lane => this.id == lane.id
    case _          => false
  }

  override def hashCode(): Int = this.id.toInt
}

object Lane extends IdProvider[Lane]
