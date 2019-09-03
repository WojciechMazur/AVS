package pl.edu.agh.wmazur.avs.model.entity.intersection.reservation

import scala.collection._

class ReservationArray(arraySize: Int) {
  import ReservationArray._
  type Reservations = Array[Option[ReservationId]]
  type ReservedTiles = mutable.Map[ReservationId, mutable.Set[TileId]]
  type ReservedTimeTiles = mutable.Map[Timestamp, mutable.Set[TileId]]

  // format: off
  protected[this] val reservationGrids: mutable.TreeMap[Timestamp, Reservations] = mutable.TreeMap.empty[Timestamp, Reservations]
  protected[this] val reservedTilesByTimestamp: mutable.TreeMap[Timestamp, ReservedTiles] = mutable.TreeMap.empty[Timestamp, ReservedTiles]
  protected[this] val reservedTimeTilesByReservation: mutable.TreeMap[ReservationId, ReservedTimeTiles] = mutable.TreeMap.empty[ReservationId, ReservedTimeTiles]
  // format: on

  def reserve(reservationId: ReservationId,
              tiles: Iterable[TimeTile]): Boolean = {
    val wasAlreadyReserved = {
      tiles.exists { t =>
        reservationGrids
          .get(t.timestamp)
          .exists(_(t.tileId).isDefined)
      }
    }

//    val firstAvailableTime = reservationGrids.headOption
//      .map {
//        case (key, _) => key
//      }
//      .getOrElse(0L)

    if (!wasAlreadyReserved) {
      tiles
//        .filter(_.timestamp >= firstAvailableTime)
        .foreach { tile =>
          val timestamp = tile.timestamp
          val tileId = tile.tileId

          reservationGrids
            .getOrElseUpdate(timestamp, Array.fill(arraySize)(None))
            .update(tileId, Some(reservationId))

          reservedTilesByTimestamp
            .getOrElseUpdate(timestamp, mutable.Map.empty)
            .getOrElseUpdate(reservationId, mutable.Set.empty)
            .update(tileId, included = true)

          reservedTimeTilesByReservation
            .getOrElseUpdate(reservationId, mutable.Map.empty)
            .getOrElseUpdate(timestamp, mutable.Set.empty)
            .update(tileId, included = true)

        }
      true
    } else {
      false
    }
  }

  def cancel(reservationId: ReservationId): Unit = {
    reservedTimeTilesByReservation.remove(reservationId).foreach { removed =>
      removed.keySet.foreach { timestamp =>
        reservedTilesByTimestamp
          .get(timestamp)
          .foreach(_.remove(reservationId))

        reservationGrids.get(timestamp).foreach { reservations =>
          removed
            .get(timestamp)
            .foreach(_.foreach(reservations.update(_, None)))
        }
      }
    }
  }

  def cleanup(currentTime: Timestamp): Unit = {
    val outdated = reservationGrids
      .takeWhile(_._1 < currentTime)
      .keySet

    reservationGrids --= outdated
    reservedTilesByTimestamp --= outdated

    val emptyKeys = reservedTimeTilesByReservation.flatMap {
      case (reservationId, timeTiles) =>
        val outdated = timeTiles.takeWhile(_._1 < currentTime).keySet
        timeTiles --= outdated

        if (timeTiles.isEmpty) { reservationId :: Nil } else { Nil }
    }
    reservedTimeTilesByReservation --= emptyKeys
  }

  def reservedTilesAt(timestamp: Timestamp): Iterable[TileId] = {
    reservedTilesByTimestamp
      .get(timestamp)
      .toIterable
      .flatMap(_.values.flatten)

  }

  def reservationAt(timestamp: Timestamp): Iterable[ReservationId] = {
    reservedTilesByTimestamp
      .get(timestamp)
      .toIterable
      .flatMap(_.keySet)
  }

  def reservationExists(reservationId: ReservationId): Boolean =
    reservedTimeTilesByReservation.contains(reservationId)

  def isRestrictedTile(tileId: TileId): Boolean = false

  def isReservedAt(timestamp: Timestamp, tileId: TileId): Boolean =
    reservationGrids.get(timestamp).exists(_(tileId).isDefined)

  def latestReservationTime(): Option[Timestamp] =
    reservationGrids.lastOption.map(_._1)

  def firstAvailableTimestamp(time: Timestamp): Timestamp = {
    time / timeStepMillis * timeStepMillis
  }

  def firstAvailableTimestamp(timeTile: TimeTile): Timestamp = {
    val availableTimestamps = reservationGrids
      .filter(_._1 > timeTile.timestamp)
      .filterNot {
        case (timestamp, _) => isReservedAt(timestamp, timeTile.tileId)
      }
      .keys
    (availableTimestamps ++
      latestReservationTime().map(_ + timeStepMillis).toList).min

  }
}

object ReservationArray {
  import scala.concurrent.duration._
  type ReservationId = Long
  type Timestamp = Long
  type TileId = Int

  //TODO Add to config
  val timeStep: FiniteDuration = 1.second / 60
  val timeStepMillis = timeStep.toMillis
  case class TimeTile(tileId: TileId, timestamp: Timestamp)

}
