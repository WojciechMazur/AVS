package pl.edu.agh.wmazur.avs.simulation.map.micro

import org.locationtech.spatial4j.context.SpatialContext
import org.locationtech.spatial4j.shape.impl.RectangleImpl
import org.scalatest.FlatSpec
import pl.edu.agh.wmazur.avs.utils.{East, North, South, West}

class TileGridSpec extends FlatSpec {
  val rec = new RectangleImpl(10, 15, 10, 15, SpatialContext.GEO)
  val grid = TilesGrid(rec, 1)

  "Tile grid" must "divide rectange into tiles" in {

    assert(grid.cellsX == 5)
    assert(grid.cellsY == 5)
    assert(grid.size == 25)
    assert(grid.size == grid.tiles.size)

    val edgeTiles = 0.to(4) ++ 20.to(24) ++ Seq(5, 10, 15) ++ Seq(9, 14, 19)
    grid.tiles.foreach { tile =>
      if (edgeTiles.contains(tile.id)) {
        assert(tile.isEdge, s"tile ${tile.id} should by edge")
      } else {
        assert(!tile.isEdge, s"tile ${tile.id} should not be edge")
      }
    }
  }

  "it" should "correctly find tile neighbour" in {
    assert(grid.getNeighbourTile(0, North).isEmpty)
    assert(grid.getNeighbourTile(20, West).isEmpty)
    assert(grid.getNeighbourTile(9, East).isEmpty)
    assert(grid.getNeighbourTile(22, South).isEmpty)

    assert(grid.getNeighbourTile(7, West).get.id == 6)
    assert(grid.getNeighbourTile(7, East).get.id == 8)
    assert(grid.getNeighbourTile(7, North).get.id == 2)
    assert(grid.getNeighbourTile(7, South).get.id == 12)
  }
}
