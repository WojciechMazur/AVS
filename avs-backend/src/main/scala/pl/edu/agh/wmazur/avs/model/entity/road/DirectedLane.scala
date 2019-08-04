package pl.edu.agh.wmazur.avs.model.entity.road

import mikera.vectorz.Vector2
import org.locationtech.jts.geom.impl.CoordinateArraySequence
import org.locationtech.jts.geom.{Coordinate, Geometry, LineString}
import org.locationtech.spatial4j.distance.DistanceUtils
import org.locationtech.spatial4j.shape.{Point, Shape}
import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.EntitySettings
import pl.edu.agh.wmazur.avs.model.entity.utils.SpatialUtils._
import pl.edu.agh.wmazur.avs.model.entity.utils.{MathUtils, SpatialUtils}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.Angle

case class DirectedLane(id: Lane#Id,
                        override val middleLine: LineString,
                        spec: LaneSpec)
    extends Lane {

  private val startPoint = middleLine.getStartPoint
  private val endPoint = middleLine.getEndPoint

  lazy val laneVector: Vector2 = Vector2
    .of(middleLine.getEndPoint.getX - middleLine.getStartPoint.getX,
        middleLine.getEndPoint.getY - middleLine.getStartPoint.getY)

  lazy val squaredLaneLength: Dimension =
    laneVector.dotProduct(laneVector).fromGeoDegrees
  lazy val length: Dimension = middleLine.getStartPoint.distance(
    middleLine.getEndPoint) * DistanceUtils.DEG_TO_KM * 1000

  lazy val heading: Angle =
    MathUtils.boundedAngle(
      startPoint.angle(Point2(endPoint.getX, endPoint.getY)))

  private val xDiff: Dimension = spec.halfWidth.fromMeters * Math.cos(
    heading + Math.PI / 2)
  private val yDiff: Dimension = spec.halfWidth.fromMeters * Math.sin(
    heading + Math.PI / 2)

  lazy val area: Shape = {
    PolygonFactory(
      points = List(
        startPoint.move(xDiff, yDiff),
        endPoint.move(xDiff, yDiff),
        endPoint.move(-xDiff, -yDiff),
        startPoint.move(-xDiff, -yDiff)
      ),
      centroid = position
    )
  }

  override val leftBorder: LineString = LineFactory(
    startPoint.move(-xDiff, -yDiff),
    endPoint.move(-xDiff, -yDiff)
  )

  override val rightBorder: LineString = LineFactory(
    startPoint.move(xDiff, yDiff),
    endPoint.move(xDiff, yDiff),
  )

  lazy val position: Point = pointAtNormalizedDistance(0.5)
  lazy val entryPoint: Point = Point2(startPoint.getX, startPoint.getY)
  lazy val exitPoint: Point = Point2(endPoint.getX, endPoint.getY)

  override def pointAtNormalizedDistance(normalizedDistance: Double): Point =
    startPoint.move(normalizedDistance * laneVector.getX.fromGeoDegrees,
                    normalizedDistance * laneVector.getY.fromGeoDegrees)

  override def distanceAlongLane(point: Point): Dimension = {
    val pointVector =
      Vector2.of(point.getX - startPoint.getX, point.getY - startPoint.getY)
    ((pointVector dotProduct laneVector) / length).fromGeoDegrees
  }

  override def normalizedDistanceAlongLane(point: Point): Double = {
    val pointVector =
      Vector2.of(point.getX - startPoint.getX, point.getY - startPoint.getY)
    (pointVector dotProduct laneVector) / squaredLaneLength.geoDegrees
  }

  override def headingAtNormalizedDistance(normalizedDistance: Double): Angle =
    heading

  override def leadPointOf(point: Point, distance: Dimension): Point = {
    val fraction = normalizedDistanceAlongLane(point)
    val p = pointAtNormalizedDistance(fraction)
    p.move(distance, heading)
  }

  override def getGeometryFraction(start: Double, end: Double): Geometry = {
    assert(start >= 0.0 && start <= 1.0,
           s"GeometryFraction must be normalized, given value $start")
    assert(end >= 0.0 && end <= 1.0,
           s"GeometryFraction must be normalized, given value $end")
    if (start == end) {
      getGeometryFraction(start - 0.0000001, end + 0.0000001)
    } else {

      val p1 = pointAtNormalizedDistance(start)
      val p2 = pointAtNormalizedDistance(end)
      shapeFactory.getGeometryFrom {
        PolygonFactory(
          List(
            PointUtils(p2).move(xDiff, yDiff),
            PointUtils(p1).move(xDiff, yDiff),
            PointUtils(p1).move(-xDiff, -yDiff),
            PointUtils(p2).move(-xDiff, -yDiff),
          ),
          Point2.middlePoint(p1, p2)
        )
      }
    }
  }

  override def distanceFromPoint(pos: Point): Dimension =
    closestDistanceSquared(middleLine, pos)

  override def leftIntersectionPoint(line: LineString): Option[Point] = {
    SpatialUtils.lineStringsIntersection(leftBorder, this.middleLine)
  }

  override def rightIntersectionPoint(line: LineString): Option[Point] =
    SpatialUtils.lineStringsIntersection(rightBorder, this.middleLine)

  override def centerIntersectionPoint(line: LineString): Option[Point] =
    SpatialUtils.lineStringsIntersection(line, this.middleLine)

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
    case None => Some(SpawnPoint(this))
  }
}

object DirectedLane extends EntitySettings[DirectedLane] {
  def simple(spec: LaneSpec,
             length: Dimension,
             offStartX: Dimension = 0d,
             offStartY: Dimension = 0d,
             heading: Angle = 0d,
  ): DirectedLane = {
    val startPoint =
      Point2(offStartX.geoDegrees, offStartY.geoDegrees)
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
