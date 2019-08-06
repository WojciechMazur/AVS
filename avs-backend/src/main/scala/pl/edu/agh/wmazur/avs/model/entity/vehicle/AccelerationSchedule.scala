package pl.edu.agh.wmazur.avs.model.entity.vehicle

import java.util.concurrent.TimeUnit

import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.vehicle.AccelerationSchedule.AccelerationTimestamp
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{
  Acceleration,
  Velocity
}
import pl.edu.agh.wmazur.avs.simulation.reservation.ReservationArray.Timestamp

import scala.annotation.tailrec
import scala.concurrent.duration._

case class AccelerationSchedule(timestamps: List[AccelerationTimestamp]) {
  def calculateFinalState(initialTime: Timestamp,
                          initialVelocity: Velocity,
                          finalTime: Timestamp): (Dimension, Velocity) = {
    val nonExpiredEvents = timestamps.dropWhile(_.time < initialTime)

    @tailrec
    def iterate(time: Timestamp,
                velocity: Velocity,
                acceleration: Acceleration,
                distance: Dimension,
                remainingTimestamps: List[AccelerationTimestamp])
      : (Dimension, Velocity) = {

      if (remainingTimestamps.isEmpty || time > finalTime) {
        val remainingDuration = (finalTime - time).millis
          .toUnit(TimeUnit.SECONDS)
        val endVelocity = velocity + acceleration * remainingDuration
        val distanceTotal = distance + remainingDuration * (velocity + endVelocity) / 2
        (distanceTotal.fromMeters, endVelocity)
      } else {
        val currentEvent = remainingTimestamps.head
        val duration =
          (currentEvent.time - time).millis.toUnit(TimeUnit.SECONDS)
        val endVelocity = velocity + acceleration * duration
        val distanceTotal = distance + (velocity + endVelocity) / 2

        iterate(
          time = currentEvent.time,
          velocity = endVelocity,
          acceleration = currentEvent.acceleration,
          distance = distanceTotal.fromMeters,
          remainingTimestamps = remainingTimestamps.tail
        )
      }
    }

    iterate(initialTime, initialVelocity, 0, 0.0, nonExpiredEvents)
  }

  def calcFinalVelocity(initialVelocity: Velocity): Velocity = {
    val (finalVelocity, _) =
      timestamps.tail.foldLeft((initialVelocity, timestamps.head)) {
        case ((velocity, prevTimestamp), currTimestamp) =>
          val duration = currTimestamp.time - prevTimestamp.time
          val accVelocity = velocity + duration * prevTimestamp.acceleration
          (accVelocity, currTimestamp)
      }
    finalVelocity
  }
}

object AccelerationSchedule {
  case class AccelerationTimestamp(acceleration: Double, time: Timestamp)
}
