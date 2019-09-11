package pl.edu.agh.wmazur.avs.simulation.reservation

import org.locationtech.spatial4j.context.SpatialContext
import org.locationtech.spatial4j.shape.impl.RectangleImpl
import org.scalatest.FlatSpec
import pl.edu.agh.wmazur.avs.model.entity.intersection.reservation.ReservationArray.TimeTile
import pl.edu.agh.wmazur.avs.model.entity.intersection.reservation.{
  ReservationGrid,
  TilesGrid
}
import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.simulation.TickSource

import scala.concurrent.duration._
class ReservationGridSpec extends FlatSpec {
  val rec = new RectangleImpl(0,
                              10.meters.asGeoDegrees,
                              0,
                              10.meters.asGeoDegrees,
                              SpatialContext.GEO)
  val tilesGrid = TilesGrid(rec, 1.0.meters)
  val grid = ReservationGrid(tilesGrid, 50.millis, TickSource.timeStep)

  "Reservation Grid" must "allow to reserve tiles in single time grid" in {
    val tiles = 0.to(5).map(id => TimeTile(id, 0))
    val wasReserved = grid.reserve(1, tiles)
    assert(wasReserved)
    0.to(5).foreach(id => assert(grid.isReservedAt(0, id)))
  }

  it must "allow to remove reservation" in {
    grid.cancel(1)
    0.to(5).foreach(id => assert(!grid.isReservedAt(0, id)))
  }

  it must "allow to reserve in multiple time grids" in {
    val idRange = 10.to(20)
    val timeRange = 0.to(10).map(_ * 10)

    val timeTiles = idRange
      .zip(timeRange)
      .map {
        case (id, time) => TimeTile(id, time)
      }

    val wasReserved = grid.reserve(2, timeTiles)
    assert(wasReserved)

    timeTiles.foreach(tt => assert(grid.isReservedAt(tt.timestamp, tt.tileId)))
  }

  it must "deny overriding reservation" in {
    val idRange = 10.to(15)
    val timeRange = 0.to(5).map(_ * 10)
    val tiles = idRange.zip(timeRange).map {
      case (id, time) => TimeTile(id, time)
    }
    val wasReserved = grid.reserve(3, tiles)
    assert(!wasReserved)
  }

  it must "allow to cleanup overdated time-tile grids" in {
    grid.cleanup(grid.cleanIntervalMillis.toInt + 1)
    0.to(5).map(_ * 10).foreach { time =>
      assert {
        grid.reservedTilesAt(time).isEmpty
      }
    }
    6.to(10).map(_ * 10).foreach { time =>
      assert {
        grid.reservedTilesAt(time).nonEmpty
      }
    }

  }
}
