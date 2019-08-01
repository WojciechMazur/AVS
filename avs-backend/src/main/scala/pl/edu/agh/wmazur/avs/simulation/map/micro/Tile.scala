package pl.edu.agh.wmazur.avs.simulation.map.micro

import org.locationtech.spatial4j.shape.{Point, Rectangle}
import pl.edu.agh.wmazur.avs.simulation.reservation.ReservationArray.TileId

case class Tile(id: TileId, rec: Rectangle, isEdge: Boolean = false) {
  lazy val center: Point = rec.getCenter

}
