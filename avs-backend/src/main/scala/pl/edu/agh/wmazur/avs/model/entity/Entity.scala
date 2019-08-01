package pl.edu.agh.wmazur.avs.model.entity

import mikera.vectorz.Vector2
import org.locationtech.spatial4j.context.SpatialContext
import org.locationtech.spatial4j.shape.{Point, Rectangle, Shape}
trait Entity extends Identifiable {
  def id: this.Id
  def area: Shape
  lazy val bufferedArea: Shape = area.getBuffered(0.0000001, SpatialContext.GEO)
  def position: Point

  type Self <: Entity
  def entitySettings: EntitySettings[Self]
}
