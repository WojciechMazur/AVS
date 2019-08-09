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

case class TilesGrid(area: Shape, granularity: Double) {
  val shape: Rectangle = area.getBoundingBox
  val (cellsX, cellWidth) = calcTileSpec(shape.getWidth)
  val (cellsY, cellHeight) = calcTileSpec(shape.getHeight)
  val size: Int = cellsX * cellsY

  def calcTileSpec(length: Double): (Int, Double) = {
    val tilesQuantity = (length / granularity).ceil.toInt
    val finalSize = length / tilesQuantity
    (tilesQuantity, finalSize)
  }

  val tiles: Vector[Tile] = {
    val idsIterator = Iterator.from(0)
    val bufferSize = granularity / 10
    for {
      bottomY <- 0
        .until(cellsY)
        .map(_ * cellHeight + shape.getMinX)
        .map(MathUtils.roundDouble(_, 2))
      leftX <- 0
        .until(cellsX)
        .map(_ * cellWidth + shape.getMinY)
        .map(MathUtils.roundDouble(_, 2))
    } yield {
      val rec = new RectangleImpl(leftX,
                                  leftX + cellWidth,
                                  bottomY,
                                  bottomY + cellWidth,
                                  SpatialContext.GEO)
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
