package pl.edu.agh.wmazur.avs.simulation.map.micro

import org.locationtech.spatial4j.shape.{Point, Rectangle}
import pl.edu.agh.wmazur.avs.simulation.reservation.ReservationArray.TileId
import pl.edu.agh.wmazur.avs.model.entity.utils.MathUtils.AVectorRound

case class Tile(id: TileId, rec: Rectangle, isEdge: Boolean = false) {
  lazy val center: Point = rec.getCenter

}
