package pl.edu.agh.wmazur.avs.model.entity

import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.prep.{
  PreparedGeometry,
  PreparedGeometryFactory
}
import org.locationtech.spatial4j.context.SpatialContext
import org.locationtech.spatial4j.shape.{Point, Shape}
import pl.edu.agh.wmazur.avs.model.entity.utils.SpatialUtils
trait Entity extends Identifiable {
  def id: this.Id
  def area: Shape
  lazy val bufferedArea: Shape = area.getBuffered(0.0000001, SpatialContext.GEO)
  lazy val geometry: Geometry = SpatialUtils.shapeFactory.getGeometryFrom(area)
  lazy val prepearedGeometry: PreparedGeometry =
    PreparedGeometryFactory.prepare(geometry)

  def position: Point

  type Self <: Entity
  def entitySettings: EntitySettings[Self]
}
