package pl.edu.agh.wmazur.avs.model.entity.utils

import mikera.vectorz.Vector2
import org.locationtech.spatial4j.shape.Point

import scala.language.implicitConversions

object SpatialUtils {
  implicit class PointAsVector(point: Point) {
    val vector: Vector2 = Vector2.of(point.getX, point.getY)
  }

  implicit def pointToVector(point: Point): Vector2 = {
    Vector2.of(point.getX, point.getY)
  }

}
