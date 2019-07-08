package pl.edu.agh.wmazur.avs.model.entity.road

import org.locationtech.jts.geom.impl.CoordinateArraySequence
import org.locationtech.jts.geom.{Coordinate, Geometry, LineString}
import org.locationtech.jts.math.Vector2D
import org.locationtech.spatial4j.shape.{Point, Shape}
import pl.edu.agh.wmazur.avs.model.entity.EntitySettings
import pl.edu.agh.wmazur.avs.model.entity.utils.SpatialUtils._
import pl.edu.agh.wmazur.avs.model.entity.utils.{MathUtils, SpatialUtils}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{Angle, Dimension}
case class DirectedLane(id: Lane#Id = DirectedLane.idProvider.getId,
                        private val line: LineString,
                        spec: LaneSpec)
    extends Lane {

  lazy val laneVector: Vector2D = Vector2D.create(
    line.getStartPoint.getCoordinate,
    line.getEndPoint.getCoordinate)
  lazy val squaredLaneLength: Dimension = laneVector.dot(laneVector)
  lazy val length: Dimension = Math.sqrt(squaredLaneLength)
  lazy val heading: Angle = MathUtils.boundedAngle(laneVector.angle)
  private val xDiff = spec.halfWidth * Math.cos(heading + Math.PI / 2)
  private val yDiff = spec.halfWidth * Math.sin(heading + Math.PI / 2)
  private val startPoint = line.getStartPoint
  private val endPoint = line.getEndPoint

  lazy val area: Shape = {
    shapeFactory
      .polygon()
      .pointXY(startPoint.getX + xDiff, startPoint.getY + yDiff)
      .pointXY(endPoint.getX + xDiff, endPoint.getY + yDiff)
      .pointXY(startPoint.getX - xDiff, startPoint.getY - yDiff)
      .pointXY(endPoint.getX - xDiff, endPoint.getY - yDiff)
      .build()
  }

  val leftBorder: Shape = shapeFactory
    .lineString()
    .pointXY(startPoint.getX - xDiff, startPoint.getY - yDiff)
    .pointXY(endPoint.getX - xDiff, endPoint.getY - yDiff)
    .build()

  val rightBorder: Shape = shapeFactory
    .lineString()
    .pointXY(startPoint.getX + xDiff, startPoint.getY + yDiff)
    .pointXY(endPoint.getX + xDiff, endPoint.getY + yDiff)
    .build()

  lazy val position: Point = area.getCenter
  lazy val entryPoint: Point = PointFactory(startPoint.getX, startPoint.getY)
  lazy val exitPoint: Point = PointFactory(endPoint.getX, endPoint.getY)

  override def pointAtNormalizedDistance(distance: Dimension): Point =
    PointFactory(
      startPoint.getX + distance * laneVector.getX,
      startPoint.getY + distance * laneVector.getY
    )

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

  override def headingAtNormalizedDistance(distance: Angle): Angle = heading

  override def leadPointOf(point: Point, distance: Dimension): Point = {
    val fraction = normalizedDistanceAlongLane(point)
    val p = pointAtNormalizedDistance(fraction)
    PointFactory(
      p.getX + distance * Math.cos(heading),
      p.getY + distance * Math.sin(heading)
    )
  }

  override def getGeometryFraction(start: Double, end: Double): Geometry = {
    assert(start > 0.0 && start < 1.0, "GeometryFraction must be normalized")
    assert(end > 0.0 && end < 1.0, "GeometryFraction must be normalized")

    val p1 = pointAtNormalizedDistance(start)
    val p2 = pointAtNormalizedDistance(end)
    shapeFactory.getGeometryFrom {
      shapeFactory
        .polygon()
        .pointXY(p1.getX + xDiff, p1.getY + yDiff)
        .pointXY(p2.getX + xDiff, p2.getY + yDiff)
        .pointXY(p1.getX - xDiff, p1.getY - yDiff)
        .pointXY(p2.getX - xDiff, p2.getY - yDiff)
        .build()
    }
  }

  override def distanceFromPoint(pos: Point): Dimension =
    closestDistanceSquared(line, pos)

  override def leftIntersectionPoint(line: LineString): Option[Point] =
    SpatialUtils.lineStringsIntersection(leftBorder.asInstanceOf[LineString],
                                         this.line)

  override def rightIntersectionPoint(line: LineString): Option[Point] =
    SpatialUtils.lineStringsIntersection(rightBorder.asInstanceOf[LineString],
                                         this.line)

  override def centerIntersectionPoint(line: LineString): Option[Point] =
    SpatialUtils.lineStringsIntersection(line, this.line)

  override type Self = DirectedLane
  override def entitySettings: EntitySettings[DirectedLane] = DirectedLane

}

object DirectedLane extends EntitySettings[DirectedLane] {
  def apply(id: Lane#Id, spec: LaneSpec)(x1: Double, y1: Double)(
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
    new DirectedLane(id, lineString, spec)
  }

  def apply(id: Lane#Id, p1: Point, p2: Point, spec: LaneSpec): DirectedLane = {
    DirectedLane(id, spec)(p1.getX, p1.getY)(p2.getX, p2.getY)
  }

}
