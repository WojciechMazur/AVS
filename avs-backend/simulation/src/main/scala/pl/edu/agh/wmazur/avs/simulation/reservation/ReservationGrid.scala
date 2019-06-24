package pl.edu.agh.wmazur.avs.simulation.reservation

import pl.edu.agh.wmazur.avs.simulation.map.micro.TilesGrid
import pl.edu.agh.wmazur.avs.simulation.reservation.ReservationArray.Timestamp

import scala.concurrent.duration.FiniteDuration

case class ReservationGrid(tilesGrid: TilesGrid,
                           cleanupInterval: FiniteDuration)
    extends ReservationArray(tilesGrid.size) {

  val cleanIntervalMillis: Long = cleanupInterval.toMillis
  private var nextCleanup = cleanIntervalMillis

  override def cleanup(currentTime: Timestamp): Unit = {
    if (currentTime >= nextCleanup) {
      super.cleanup(currentTime)
      nextCleanup += cleanIntervalMillis
    }
  }
}
