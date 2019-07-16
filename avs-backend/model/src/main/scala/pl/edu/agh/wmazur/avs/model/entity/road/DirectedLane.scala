package pl.edu.agh.wmazur.avs.model.entity.road

import org.locationtech.jts.geom.impl.CoordinateArraySequence
import org.locationtech.jts.geom.{Coordinate, Geometry, LineString}
import org.locationtech.jts.math.Vector2D
import org.locationtech.spatial4j.shape.{Point, Shape}
import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.EntitySettings
import pl.edu.agh.wmazur.avs.model.entity.utils.SpatialUtils._
import pl.edu.agh.wmazur.avs.model.entity.utils.{MathUtils, SpatialUtils}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.Angle

import scala.concurrent.duration._

case class DirectedLane(id: Lane#Id,
                        private val line: LineString,
                        spec: LaneSpec)
    extends Lane {

  lazy val laneVector: Vector2D = Vector2D.create(
    line.getStartPoint.getCoordinate,
    line.getEndPoint.getCoordinate)
  lazy val squaredLaneLength: Dimension = laneVector.dot(laneVector)
  lazy val length: Dimension = squaredLaneLength.sqrt
  lazy val heading: Angle = MathUtils.boundedAngle(laneVector.angle)
  private val xDiff: Dimension = spec.halfWidth * Math.cos(
    heading + Math.PI / 2)
  private val yDiff: Dimension = spec.halfWidth * Math.sin(
    heading + Math.PI / 2)
  private val startPoint = line.getStartPoint
  private val endPoint = line.getEndPoint

  lazy val area: Shape = {
    PolygonFactory(
      points = List(
        startPoint.move(xDiff, yDiff),
        endPoint.move(xDiff, yDiff),
        startPoint.move(-xDiff, -yDiff),
        endPoint.move(-xDiff, -yDiff)
      ),
      centroid = position
    )
  }

  val leftBorder: Shape = LineFactory(
    startPoint.move(-xDiff, -yDiff),
    endPoint.move(-xDiff, -yDiff)
  )

  val rightBorder: Shape = LineFactory(
    startPoint.move(xDiff, yDiff),
    endPoint.move(xDiff, yDiff),
  )

  lazy val position: Point = pointAtNormalizedDistance(0.5)
  lazy val entryPoint: Point = Point2(startPoint.getX, startPoint.getY)
  lazy val exitPoint: Point = Point2(endPoint.getX, endPoint.getY)

  override def pointAtNormalizedDistance(distance: Dimension): Point =
    startPoint.move(distance * laneVector.getX, distance * laneVector.getY)

  override def distanceAlongLane(point: Point): Dimension = {
    val pointVector = Vector2D.create(point.getX - startPoint.getX,
                                      point.getY - startPoint.getY)
    (pointVector dot laneVector) / length
  }

  override def normalizedDistanceAlongLane(point: Point): Dimension = {
    val pointVector = Vector2D.create(point.getX - startPoint.getX,
                                      point.getY - startPoint.getY)
    (pointVector dot laneVector) / squaredLaneLength
  }

  override def headingAtNormalizedDistance(distance: Dimension): Angle = heading

  override def leadPointOf(point: Point, distance: Dimension): Point = {
    val fraction = normalizedDistanceAlongLane(point)
    val p = pointAtNormalizedDistance(fraction)
    p.move(distance, heading)
  }

  override def getGeometryFraction(start: Double, end: Double): Geometry = {
    assert(start >= 0.0 && start <= 1.0, "GeometryFraction must be normalized")
    assert(end >= 0.0 && end <= 1.0, "GeometryFraction must be normalized")

    val p1 = pointAtNormalizedDistance(start)
    val p2 = pointAtNormalizedDistance(end)
    shapeFactory.getGeometryFrom {
      PolygonFactory(
        List(
          PointUtils(p1).move(xDiff, yDiff),
          PointUtils(p2).move(xDiff, yDiff),
          PointUtils(p1).move(-xDiff, -yDiff),
          PointUtils(p2).move(-xDiff, -yDiff),
        ),
        Point2.middlePoint(p1, p2)
      )
    }
  }

  override def distanceFromPoint(pos: Point): Dimension =
    closestDistanceSquared(line, pos)

  override def leftIntersectionPoint(line: LineString): Option[Point] = {
    SpatialUtils.lineStringsIntersection(leftBorder, this.line)
  }

  override def rightIntersectionPoint(line: LineString): Option[Point] =
    SpatialUtils.lineStringsIntersection(rightBorder, this.line)

  override def centerIntersectionPoint(line: LineString): Option[Point] =
    SpatialUtils.lineStringsIntersection(line, this.line)

  override type Self = DirectedLane
  override def entitySettings: EntitySettings[DirectedLane] = DirectedLane

  override val collectorPoint: Option[CollectorPoint] =
    spec.leadsIntoLane match {
      case Some(_) => None
      case None    => Some(CollectorPoint(this))
    }

  override val spawnPoint: Option[SpawnPoint] = spec.leadsFromLane match {
    case Some(_) => None
    //Todo Config
    case None => Some(SpawnPoint(this, 1.minute / 60))
  }
}

object DirectedLane extends EntitySettings[DirectedLane] {
  def simple(spec: LaneSpec,
             length: Dimension,
             offStartX: Dimension = 0d,
             offStartY: Dimension = 0d,
             heading: Angle = 0d,
  ): DirectedLane = {
    val startPoint = Point2(offStartX.geoDegrees, offStartY.geoDegrees)
    val endPoint = startPoint.moveRotate(length, heading)
    DirectedLane(spec)(startPoint, endPoint)
  }

  def apply(spec: LaneSpec,
            x1: Double,
            y1: Double,
            x2: Double,
            y2: Double): DirectedLane = {
    val coords = Array(
      new Coordinate(x1, y1),
      new Coordinate(x2, y2)
    )
    val lineString = new LineString(
      new CoordinateArraySequence(coords),
      SpatialUtils.shapeFactory.getGeometryFactory
    )
    new DirectedLane(Lane.nextId, lineString, spec.copy())
  }

  def apply(spec: LaneSpec)(p1: Point, p2: Point): DirectedLane = {
    DirectedLane(spec, p1.getX, p1.getY, p2.getX, p2.getY)
  }

}
