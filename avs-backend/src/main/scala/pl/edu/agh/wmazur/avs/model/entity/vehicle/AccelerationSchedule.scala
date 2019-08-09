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
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._

case class AccelerationSchedule(timestamps: List[AccelerationTimestamp]) {
  def calculateFinalState(initialTime: Timestamp,
                          initialVelocity: Velocity,
                          finalTime: Timestamp): (Dimension, Velocity) = {
    val nonExpiredEvents = timestamps.dropWhile(_.timeStart < initialTime)

    @tailrec
    def iterate(time: Timestamp,
                velocity: Velocity,
                acceleration: Acceleration,
                distance: Dimension,
                remainingTimestamps: List[AccelerationTimestamp])
      : (Dimension, Velocity) = {

      if (remainingTimestamps.isEmpty || remainingTimestamps.head.timeEnd > finalTime) {
        val duration = (finalTime - time).millis
          .toUnit(TimeUnit.SECONDS)

        val endVelocity = velocity + acceleration * duration
        val distanceTotal = distance + duration * (velocity + endVelocity) / 2
        (distanceTotal.meters, endVelocity)
      } else {
        val currentEvent = remainingTimestamps.head
        val duration = currentEvent.durationSeconds

        val endVelocity = velocity + acceleration * duration
        val distanceTotal = distance + duration * (velocity + endVelocity) / 2

        iterate(
          time = currentEvent.timeEnd,
          velocity = endVelocity,
          acceleration = currentEvent.acceleration,
          distance = distanceTotal.meters,
          remainingTimestamps = remainingTimestamps.tail
        )
      }
    }

    iterate(initialTime,
            initialVelocity,
            nonExpiredEvents.headOption.map(_.acceleration).getOrElse(0),
            0.0,
            nonExpiredEvents)
  }

  def calcFinalVelocity(initialVelocity: Velocity): Velocity = {
    val (finalVelocity, _) =
      timestamps.tail.foldLeft((initialVelocity, timestamps.head)) {
        case ((velocity, prevTimestamp), currTimestamp) =>
          val duration = currTimestamp.timeStart - prevTimestamp.timeStart
          val accVelocity = velocity + duration * prevTimestamp.acceleration
          (accVelocity, currTimestamp)
      }
    finalVelocity
  }
}

object AccelerationSchedule {
  private case class SimpleEvent(acceleration: Acceleration,
                                 timestamp: Timestamp)

  case class Builder() {
    private val events: ListBuffer[SimpleEvent] = ListBuffer.empty

    def add(acceleration: Acceleration, time: Timestamp): Builder = {
      events += SimpleEvent(acceleration, time)
      this
    }

    def build: AccelerationSchedule = {
      AccelerationSchedule(
        (events :+ events.last)
          .sliding(2, 1)
          .map(_.toList)
          .map {
            case List(current, next) =>
              AccelerationTimestamp(current.acceleration,
                                    current.timestamp,
                                    next.timestamp)
          }
          .toList
      )
    }
  }

  case class AccelerationTimestamp(acceleration: Double,
                                   timeStart: Timestamp,
                                   timeEnd: Timestamp) {
    def duration: FiniteDuration = (timeEnd - timeStart).millis
    def durationSeconds: Double = duration.toUnit(TimeUnit.SECONDS)
    def durationMillis: Long = duration.toMillis
  }
}
