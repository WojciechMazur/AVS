package pl.edu.agh.wmazur.avs.model.entity.intersection.reservation

import pl.edu.agh.wmazur.avs.model.entity.intersection.reservation.ReservationArray.{
  TileId,
  Timestamp
}

import scala.concurrent.duration.FiniteDuration

case class ReservationGrid(tilesGrid: TilesGrid,
                           cleanupInterval: FiniteDuration)
    extends ReservationArray(tilesGrid.size) {

  override def isRestrictedTile(tileId: TileId): Boolean =
    tilesGrid.tilesOutsideArea.contains(tileId)

  val cleanIntervalMillis: Long = cleanupInterval.toMillis
  private var nextCleanup = cleanIntervalMillis

  override def cleanup(currentTime: Timestamp): Unit = {
    if (currentTime >= nextCleanup) {
      super.cleanup(currentTime)
      nextCleanup += cleanIntervalMillis
    }
  }
}
