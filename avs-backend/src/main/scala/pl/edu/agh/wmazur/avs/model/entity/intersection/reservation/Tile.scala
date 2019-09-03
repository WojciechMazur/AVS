package pl.edu.agh.wmazur.avs.model.entity.intersection.reservation

import org.locationtech.jts.geom.prep.{
  PreparedGeometry,
  PreparedGeometryFactory
}
import org.locationtech.spatial4j.shape.{Point, Rectangle}
import pl.edu.agh.wmazur.avs.model.entity.intersection.reservation.ReservationArray.TileId
import pl.edu.agh.wmazur.avs.model.entity.utils.SpatialUtils

case class Tile(id: TileId, rec: Rectangle, isEdge: Boolean = false) {
  lazy val center: Point = rec.getCenter
  lazy val preparedGeometry: PreparedGeometry =
    new PreparedGeometryFactory().create {
      SpatialUtils.shapeFactory.getGeometryFrom(rec)
    }
}
