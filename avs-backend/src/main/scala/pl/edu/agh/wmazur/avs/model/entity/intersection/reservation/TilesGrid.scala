package pl.edu.agh.wmazur.avs.model.entity.intersection.reservation

import org.locationtech.spatial4j.context.SpatialContext
import org.locationtech.spatial4j.shape.impl.RectangleImpl
import org.locationtech.spatial4j.shape.{Rectangle, Shape, SpatialRelation}
import pl.edu.agh.wmazur.avs.model.entity.intersection.reservation.ReservationArray.TileId
import pl.edu.agh.wmazur.avs.model.entity.utils.MathUtils
import pl.edu.agh.wmazur.avs.simulation.utils.BasicDirection
import pl.edu.agh.wmazur.avs.simulation.utils.BasicDirection.{
  East,
  North,
  South,
  West
}
import pl.edu.agh.wmazur.avs.Dimension

case class TilesGrid(area: Shape, granularity: Dimension) {
  val shape: Rectangle = area.getBoundingBox
  val (cellsX, cellWidth) = calcTileSpec(shape.getWidth.geoDegrees)
  val (cellsY, cellHeight) = calcTileSpec(shape.getHeight.geoDegrees)
  val size: Int = cellsX * cellsY
  println(
    f"Intersection grid ${cellsX}x$cellsY. Controlled area ${cellsX * cellWidth.asMeters}%3.2fm x ${cellsY * cellHeight.asMeters}%3.2fm")

  def calcTileSpec(length: Dimension): (Int, Dimension) = {
    val tilesQuantity = (length.asMeters / granularity.asMeters).ceil.toInt
    val finalSize = length.asMeters / tilesQuantity
    (tilesQuantity, finalSize.meters)
  }

  val tiles: Vector[Tile] = {
    val idsIterator = Iterator.from(0)
    val bufferSize = (granularity / 10.0).meters.asGeoDegrees
    for {
      cellY <- 0.until(cellsY)
      minY = cellY * cellHeight.asGeoDegrees + shape.getMinY
      maxY = minY + cellHeight.asGeoDegrees
      cellX <- 0.until(cellsX)
      minX = cellX * cellWidth.asGeoDegrees + shape.getMinX
      maxX = minX + cellWidth.asGeoDegrees
    } yield {
      val rec = new RectangleImpl(minX, maxX, minY, maxY, SpatialContext.GEO)
      val id = idsIterator.next()
      val isEdge = rec
        .getBuffered(bufferSize, SpatialContext.GEO)
        .relate(shape) == SpatialRelation.INTERSECTS

      Tile(id, rec, isEdge)
    }
  }.toVector

  val tilesOutsideArea: Set[TileId] =
    tiles
      .filterNot(_.rec.relate(area).intersects())
      .map(_.id)(scala.collection.breakOut)

  def getNeighbourTile(tile: Tile, direction: BasicDirection): Option[Tile] = {
    getNeighbourTile(tile.id, direction)
  }

  def getNeighbourTile(tileId: TileId,
                       direction: BasicDirection): Option[Tile] = {
    val offset = direction match {
      case North => -cellsX
      case South => +cellsX
      case West  => -1
      case East  => +1
    }

    val expectedTileId = tileId + offset
    Some(direction) collect {
      case North if expectedTileId >= 0    => tiles(expectedTileId)
      case South if expectedTileId <= size => tiles(expectedTileId)
      case West if tileId % cellsX != 0    => tiles(expectedTileId)
      case East if tileId % cellsX != cellsX - 1 =>
        tiles(expectedTileId)
    }
  }

  def occupiedByShape(shape: Shape): Seq[Tile] = {
    tiles.filter(_.rec.relate(shape).intersects())
  }
}
