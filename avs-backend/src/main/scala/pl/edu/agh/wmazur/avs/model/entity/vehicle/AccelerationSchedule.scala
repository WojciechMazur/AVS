package pl.edu.agh.wmazur.avs.model.entity.vehicle

import java.util.concurrent.TimeUnit
import pl.edu.agh.wmazur.avs.model.entity.utils.MathUtils.DoubleUtils
import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.vehicle.AccelerationSchedule.AccelerationTimestamp
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{
  Acceleration,
  Velocity
}
import com.softwaremill.quicklens._
import pl.edu.agh.wmazur.avs.model.entity.intersection.reservation.ReservationArray.Timestamp
import pl.edu.agh.wmazur.avs.model.entity.utils.MathUtils.DoubleUtils
import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._

case class AccelerationSchedule(timestamps: List[AccelerationTimestamp],
                                isBeingFollowed: Boolean = false) {
  def calculateFinalStateAtTime(
      initialTime: Timestamp,
      initialVelocity: Velocity,
      finalTime: Timestamp
  ): (Dimension, Velocity) = {
    val nonExpiredEvents = timestamps.dropWhile(_.timeStart < initialTime)

    @tailrec
    def iterate(time: Timestamp,
                velocity: Velocity,
                distance: Dimension,
                remainingTimestamps: List[AccelerationTimestamp])
      : (Dimension, Velocity) = {

      remainingTimestamps match {
        case Nil => (distance, velocity)
        case current :: _ if current.timeEnd > finalTime =>
          val duration = (finalTime - time).millis
            .toUnit(TimeUnit.SECONDS)

          val endVelocity = velocity + current.acceleration * duration
          val distanceTotal = distance + (duration * (velocity + endVelocity) / 2)
            .meters
          (distanceTotal, endVelocity)
        case current :: remaining =>
          val duration = current.durationSeconds

          val endVelocity = velocity + current.acceleration * duration
          val distanceTotal = distance + (duration * (velocity + endVelocity) / 2).meters

          iterate(
            time = current.timeEnd,
            velocity = endVelocity,
            distance = distanceTotal,
            remainingTimestamps = remaining
          )
      }
    }

    iterate(initialTime, initialVelocity, 0.0, nonExpiredEvents)
  }

  def calcRemaining(initialTime: Timestamp,
                    initialVelocity: Velocity): (FiniteDuration, Dimension) = {
    timestamps
      .dropWhile(_.timeEnd < initialTime)
      .foldLeft((initialTime, Duration.Zero, 0.meters, initialVelocity)) {
        case ((currentTime, accDuration, accDistance, currentVelocity),
              event) =>
          val duration = if (currentTime != event.timeStart) {
            (event.timeEnd - currentTime).millis
          } else { event.duration }
          val durationSecods = duration.toUnit(TimeUnit.SECONDS)
          val endVelocity = currentVelocity + event.acceleration * durationSecods
          val distanceTotal = accDistance + (durationSecods * (currentVelocity + endVelocity) / 2)
            .meters

          (event.timeEnd, accDuration + duration, distanceTotal, endVelocity)
      } match {
      case (_, duration, distance, _) =>
        (duration, distance)
    }
  }

  def adjustSchedule(deleyTime: FiniteDuration): AccelerationSchedule = {
//    assert(deleyTime >= Duration.Zero)
    @tailrec
    def iterate(
        catchUpTime: FiniteDuration,
        remainingEvents: List[AccelerationTimestamp],
        fixedEvents: List[AccelerationTimestamp]): AccelerationSchedule = {
      remainingEvents match {
        case _ if catchUpTime == deleyTime =>
          AccelerationSchedule(fixedEvents ++ remainingEvents)
        case Nil => AccelerationSchedule(fixedEvents)

        case current :: tail if current.acceleration.isZero =>
          current.duration match {
            case dur if dur >= deleyTime - catchUpTime =>
              val updatedCurrent = current
                .modify(_.timeStart)
                .using(_ + deleyTime.toMillis)
              iterate(deleyTime, tail, fixedEvents :+ updatedCurrent)
            case duration =>
              val availableDuration = deleyTime - duration
              //remove current event
              iterate(catchUpTime + availableDuration, tail, fixedEvents)
          }
        case current :: tail =>
          val updatedCurrent = current
            .modifyAll(_.timeStart, _.timeEnd)
            .using(_ + (deleyTime - catchUpTime).toMillis)
          iterate(catchUpTime, tail, fixedEvents :+ updatedCurrent)
      }
    }
    iterate(Duration.Zero, timestamps, Nil)
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

  def calcTimeNeededToDriveDistance(
      distance: Dimension,
      currentTime: Timestamp,
      initialVelocity: Velocity): FiniteDuration = {
    val events = timestamps
      .dropWhile(_.timeEnd < currentTime)
    val durationBeforeSchedule =
      (timestamps.head.timeStart - currentTime).max(0L).millis
    val distanceBeforeSchedule = durationBeforeSchedule.toUnit(TimeUnit.SECONDS) * initialVelocity

    val (durationTotal, _, _) = events
      .foldLeft(
        (Duration.Zero, distance - distanceBeforeSchedule, initialVelocity)) {
        case ((totalTime, remainingDistance, velocity), _)
            if remainingDistance.asMeters.isZero =>
          (totalTime, remainingDistance, velocity)
        case ((totalTime, remainingDistance, velocity), event) =>
          val duration = event.durationSeconds
          val velocityDelta = event.acceleration * duration
          val avgVelocity = (2 * velocity + velocityDelta) / 2
          val distanceTotal = duration * avgVelocity
          //          val velocityAfter = velocity + velocityDelta
          if (distanceTotal > remainingDistance.asMeters) {
            val normalizedDistance = remainingDistance.asMeters / distanceTotal
            val velocityAtEnd = velocity + normalizedDistance * velocityDelta
            val vAvg = (velocity + velocityAtEnd) / 2
            val timeToEnd = remainingDistance.asMeters / vAvg
            (totalTime + timeToEnd.seconds, 0.0.meters, velocityAtEnd)
          } else {
            (totalTime + event.duration,
             (distance.asMeters - distanceTotal).meters,
             velocity + velocityDelta)
          }
      }
    durationTotal + durationBeforeSchedule
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
