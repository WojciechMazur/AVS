package pl.edu.agh.wmazur.avs.model.entity.utils

import mikera.vectorz.Vector2
import org.locationtech.jts.geom.impl.CoordinateArraySequence
import org.locationtech.jts.geom.{Coordinate, LineString, Point => JtsPoint}
import org.locationtech.spatial4j.context.SpatialContext
import org.locationtech.spatial4j.context.jts.{
  JtsSpatialContext,
  JtsSpatialContextFactory
}
import org.locationtech.spatial4j.shape.Point
import org.locationtech.spatial4j.shape.impl.PointImpl
import org.locationtech.spatial4j.shape.jts.JtsShapeFactory
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.Angle

import scala.language.implicitConversions
import scala.collection.breakOut

object SpatialUtils {
  implicit val shapeFactory: JtsShapeFactory = new JtsShapeFactory(
    JtsSpatialContext.GEO,
    new JtsSpatialContextFactory()
  )

  implicit class PointAsVector(point: Point) {
    val vector: Vector2 = Vector2.of(point.getX, point.getY)
  }

  implicit def pointToVector(point: Point): Vector2 = {
    Vector2.of(point.getX, point.getY)
  }

  object PointFactory {
    def apply(x: Double, y: Double): Point = {
      new PointImpl(x, y, SpatialContext.GEO)
    }
  }

  object LineFactory {
    def apply(start: Point, second: Point, tail: Point*): LineString = {
      val coords: Array[Coordinate] = (start :: second :: Nil ++ tail)
        .map { point =>
          new Coordinate(point.getX, point.getY)
        }(breakOut)

      new LineString(new CoordinateArraySequence(coords),
                     shapeFactory.getGeometryFactory)
    }

    def apply(start: Point, end: Point): LineString = {
      LineFactory.apply(start, end)
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

  def lineStringsIntersection(ls1: LineString, ls2: LineString): Option[Point] =
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
        Some(PointFactory(x, y))
      case det if det == 0 =>
        val points = Vector(
          PointFactory(x1, y1),
          PointFactory(x2, y2),
          PointFactory(x3, y3),
          PointFactory(x4, y4)
        )

        val line1 = LineFactory(points(0), points(1))
        val line2 = LineFactory(points(2), points(3))

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
