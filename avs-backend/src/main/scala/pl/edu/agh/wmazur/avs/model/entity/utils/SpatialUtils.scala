package pl.edu.agh.wmazur.avs.model.entity.utils

import mikera.vectorz.Vector2
import org.locationtech.jts.algorithm.Centroid
import org.locationtech.jts.geom.impl.CoordinateArraySequence
import org.locationtech.jts.geom.util.LineStringExtracter
import org.locationtech.jts.geom.{Coordinate, LineString, LinearRing}
import org.locationtech.spatial4j.context.SpatialContext
import org.locationtech.spatial4j.context.jts.{
  JtsSpatialContext,
  JtsSpatialContextFactory
}
import org.locationtech.spatial4j.distance.DistanceUtils
import org.locationtech.spatial4j.shape.impl.PointImpl
import org.locationtech.spatial4j.shape.jts.JtsShapeFactory
import org.locationtech.spatial4j.shape.{Point, Shape}
import pl.edu.agh.wmazur.avs
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.Angle

import scala.collection.{breakOut, immutable}
import scala.language.implicitConversions

object SpatialUtils {
  implicit val shapeFactory: JtsShapeFactory = new JtsShapeFactory(
    JtsSpatialContext.GEO,
    new JtsSpatialContextFactory()
  )

  implicit class PointAsVector(point: Point) {
    val vector: Vector2 = Vector2.of(point.getX, point.getY)
  }

  implicit def geomToPointUtils(
      point: org.locationtech.jts.geom.Point): PointUtils =
    PointUtils(Point2(point.getX, point.getY))

  implicit class CoordinateUtils(self: Coordinate) {
    def angle(that: Coordinate): Angle = Math.atan2(
      that.y - self.y,
      that.x - self.x
    )
    def angle(that: Point): Angle = Math.atan2(
      that.y - self.y,
      that.x - self.x
    )
  }

  implicit class PointUtils(point: Point) {
    def simpleCoordinates: Vector2 = Vector2.of(
      point.x * DistanceUtils.DEG_TO_KM * 1000,
      point.y * DistanceUtils.DEG_TO_KM * 1000
    )

    def angle(that: Point): Angle = Math.atan2(
      that.y - point.y,
      that.x - point.x
    )

    def moveRotate(dimension: avs.Dimension, angle: Angle): Point = {
      Point2(
        point.x + dimension.geoDegrees * Math.cos(angle),
        point.y + dimension.geoDegrees * Math.sin(angle)
      )
    }

    def moveInternal(x: Double, y: Double): Point = Point2(
      point.x + x,
      point.y + y
    )

    def move(xDelta: avs.Dimension, yDelta: avs.Dimension): Point =
      moveInternal(xDelta.geoDegrees, yDelta.geoDegrees)

    //scalastyle:off
    def +(dim: avs.Dimension): Point = {
      Point2(point.x + dim.geoDegrees, point.y + dim.geoDegrees)
    }
    def -(dim: avs.Dimension): Point = {
      Point2(point.x - dim.geoDegrees, point.y - dim.geoDegrees)
    }

    //scalastyle:on

    def angleDegrees(that: Point): Angle = angle(that) * 180 / Math.PI
  }

  implicit def pointToVector(point: Point): Vector2 = {
    Vector2.of(point.getX, point.getY)
  }

  object Point2 {
    def apply(x: Double, y: Double): Point = {
      new PointImpl(x, y, SpatialContext.GEO)
    }
    def middlePoint(p1: Point, p2: Point): Point = {
      Point2(
        (p1.getX + p2.getX) / 2,
        (p1.getY + p2.getY) / 2
      )
    }
  }

  object LineGeometryFactory {
    def apply(start: Point,
              second: Point,
              tail: Seq[Point] = Seq.empty): LineString = {
      val coords: Array[Coordinate] = (start :: second :: Nil ++ tail)
        .map { point =>
          new Coordinate(point.getX, point.getY)
        }(breakOut)

      new LineString(new CoordinateArraySequence(coords),
                     shapeFactory.getGeometryFactory)
    }

    def apply(start: Coordinate, end: Coordinate): LineString = {
      new LineString(new CoordinateArraySequence(Array(start, end)),
                     shapeFactory.getGeometryFactory)
    }
  }

  object LineFactory {
    def apply(start: Point, second: Point, tail: Point*): Shape = {
      (start :: second :: Nil ++ tail).foldLeft {
        shapeFactory.lineString
      } {
        case (builder, point) => builder.pointXY(point.getX, point.getY)
      }
    }.build()
  }

  object PolygonFactory {
    def apply(points: Iterable[Point], centroid: Point): Shape = {
      val pointsSorted = SpatialUtils.pointsSorted(points, centroid)

      (pointsSorted ++ (pointsSorted.head :: Nil))
        .foldLeft(shapeFactory.polygon()) {
          case (polygon, point) => polygon.pointXY(point.x, point.y)
        }
        .build()
    }

  }

  def closestDistance(lineString: LineString, point: Point): Double = {
    val (lsx, lsy) =
      (lineString.getStartPoint.getX, lineString.getStartPoint.getY)
    val (lex, ley) = (lineString.getEndPoint.getX, lineString.getEndPoint.getY)
    val (px, py) = (point.getX, point.getY)

    val xVector = lex - lsx
    val yVector = ley - lsy

    val dotProduct = px * xVector + py * yVector
    val distance = if (dotProduct <= 0.0) {
      xVector * xVector + yVector * yVector
    } else {
      val xVec = xVector - px
      val yVec = yVector - py

      val dotProd = px * xVec + py * yVec
      val projectedLength = if (dotProd <= 0.0) {
        0.0
      } else {
        dotProd * dotProd / (xVec * xVec + yVec * yVec)
      }
      xVec * xVec + yVec * yVec - projectedLength
    }
    distance.max(0)
  }

  def closestDistanceSquared(lineString: LineString, point: Point): Double = {
    Math.sqrt(closestDistance(lineString, point))
  }

  def coordinatesSorted(coordinates: Iterable[Coordinate],
                        centroid: Point): immutable.Seq[Coordinate] = {
    coordinates
      .map { point =>
        point -> point.angle(centroid)
      }
      .toVector
      .sortBy { case (_, angle) => angle }
      .map { case (point, _) => point }
  }
  def pointsSorted(points: Iterable[Point],
                   centroid: Point): immutable.Seq[Point] = {
    val x = points.map { point =>
      point -> point.angle(centroid)
    }.toVector
    val y = x
      .sortBy { case (_, angle) => angle }
    y.map { case (point, _) => point }
  }

  def lineStringsIntersection(shape: Shape, line: LineString): Option[Point] = {
    val geometry = shapeFactory.getGeometryFrom(shape)
    val lineString = LineStringExtracter
      .getLines(geometry)
      .asInstanceOf[java.util.List[LineString]]
      .get(0)

    lineStringsIntersection(lineString, line)
  }

  def lineStringsIntersection(ls1: LineString, ls2: LineString): Option[Point] =
    if (ls1.intersects(ls2)) {
      linesIntersection(
        ls1.getStartPoint.getX,
        ls1.getStartPoint.getY,
        ls1.getEndPoint.getX,
        ls1.getEndPoint.getY,
        ls2.getStartPoint.getX,
        ls2.getStartPoint.getY,
        ls2.getEndPoint.getX,
        ls2.getEndPoint.getY
      )
    } else {
      None
    }

  def linesIntersection(x1: Double,
                        y1: Double,
                        x2: Double,
                        y2: Double,
                        x3: Double,
                        y3: Double,
                        x4: Double,
                        y4: Double): Option[Point] = {
    def determinant(a: Double, b: Double, c: Double, d: Double): Double =
      a * d - b * c

    determinant(
      x1 - x2,
      y1 - y2,
      x3 - x4,
      y3 - y4
    ) match {
      case det if det != 0 =>
        val det12 = determinant(x1, y1, x2, y2)
        val det34 = determinant(x3, y3, x4, y4)

        val x = determinant(det12, x1 - x2, det34, x3 - x4) / det
        val y = determinant(det12, y1 - y2, det34, y3 - y4) / det
        Some(Point2(x, y))
      case det if det == 0 =>
        val points = Vector(
          Point2(x1, y1),
          Point2(x2, y2),
          Point2(x3, y3),
          Point2(x4, y4)
        )

        val line1 = LineGeometryFactory(points(0), points(1))
        val line2 = LineGeometryFactory(points(2), points(3))

        points
          .foldLeft((Double.MaxValue, Option.empty[Point])) {
            case (notChanged @ (minDistance, _), point) =>
              val jtsPoint = shapeFactory.getGeometryFrom(point)
              if (closestDistanceSquared(line1, point) == 0 &&
                  closestDistanceSquared(line2, point) == 0) {
                val distance = line1.getStartPoint.distance(jtsPoint)
                if (distance < minDistance) {
                  (distance, Some(point))
                } else {
                  notChanged
                }
              } else {
                notChanged
              }
          }
          ._2
    }
  }

}
