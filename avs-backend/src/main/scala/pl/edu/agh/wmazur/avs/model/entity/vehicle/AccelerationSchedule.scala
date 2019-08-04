package pl.edu.agh.wmazur.avs.model.entity.vehicle

import java.util.concurrent.TimeUnit

import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.vehicle.AccelerationSchedule.AccelerationTimestamp
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{
  Acceleration,
  Velocity
}
import pl.edu.agh.wmazur.avs.simulation.reservation.ReservationArray.Timestamp

import scala.concurrent.duration._
import scala.annotation.tailrec
import scala.concurrent.duration.FiniteDuration

case class AccelerationSchedule(timestamps: List[AccelerationTimestamp]) {
  def calculateFinalState(initialTime: Timestamp,
                          initialVelocity: Velocity,
                          finalTime: Timestamp): (Dimension, Velocity) = {
    val nonExpiredEvents = timestamps.dropWhile(_.time < initialTime)
    @tailrec
    def iterate(
        time: Timestamp,
        velocity: Velocity,
        acceleration: Acceleration,
        distance: Dimension,
        timestamps: List[AccelerationTimestamp]): (Dimension, Velocity) = {
      if (timestamps.isEmpty) {
        val remainingDuration =
          (finalTime - time).millis.toUnit(TimeUnit.SECONDS)
        val finalVelocity = velocity + acceleration * remainingDuration
        val distanceTotal = distance + remainingDuration * (velocity + finalVelocity) / 2
        (distanceTotal, finalVelocity)
      } else {
        ???
      }
    }
  }
}

object AccelerationSchedule {
  case class AccelerationTimestamp(acceleration: Double, time: Timestamp)
}
