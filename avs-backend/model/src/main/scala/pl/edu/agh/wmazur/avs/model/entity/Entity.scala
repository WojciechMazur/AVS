package pl.edu.agh.wmazur.avs.model.entity

import org.locationtech.spatial4j.shape.{Point, Shape, Rectangle}
trait Entity extends Identifiable {
  def id: Id
  def area: Shape
  def position: Point = area.getCenter

}
