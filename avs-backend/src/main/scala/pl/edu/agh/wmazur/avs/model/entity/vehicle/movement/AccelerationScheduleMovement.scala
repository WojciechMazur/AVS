package pl.edu.agh.wmazur.avs.model.entity.vehicle.movement

import java.util.concurrent.TimeUnit

import com.softwaremill.quicklens._
import pl.edu.agh.wmazur.avs.model.entity.intersection.reservation.ReservationArray.Timestamp
import pl.edu.agh.wmazur.avs.model.entity.utils.MathUtils.DoubleUtils
import pl.edu.agh.wmazur.avs.model.entity.vehicle.AccelerationSchedule.AccelerationTimestamp
import pl.edu.agh.wmazur.avs.model.entity.vehicle.movement.VehicleMovement.{
  ScheduledVehicleMovement,
  TimeDeltaSeconds
}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.{
  AccelerationSchedule,
  Vehicle
}

import scala.annotation.tailrec
import scala.concurrent.duration._

trait AccelerationScheduleMovement extends ScheduledVehicleMovement {
  self: Vehicle with VelocityReachingMovement =>

  def accelerationSchedule: Option[AccelerationSchedule]
  def withAccelerationSchedule(
      accelerationSchedule: Option[AccelerationSchedule]): self.type

  def move(currentTime: Timestamp, timeDelta: TimeDeltaSeconds): self.type = {
    accelerationSchedule match {
      case Some(_) =>
        moveWithSchedule(currentTime, timeDelta)
      case _ => move(timeDelta)
    }
  }

  override def moveWithSchedule(currentTime: Timestamp,
                                timeDelta: TimeDeltaSeconds): self.type = {
    require(accelerationSchedule.isDefined, "No acceleration schedule")

    val schedule = accelerationSchedule.get
    val (state, updatedSchedule) =
      internalMoveWithSchedule(currentTime, timeDelta, schedule)

    state
      .withAccelerationSchedule(
        updatedSchedule.map(_.copy(isBeingFollowed = true)))
      .asInstanceOf[self.type]
  }

  private def seconds(value: Long) = value.millis.toUnit(TimeUnit.SECONDS)

  @tailrec
  private def internalMoveWithSchedule(currentTime: Timestamp,
                                       timeDelta: TimeDeltaSeconds,
                                       schedule: AccelerationSchedule)
    : (self.type, Option[AccelerationSchedule]) = {
    val events = schedule.timestamps

    if (schedule.timestamps.isEmpty) {
      (self.withAcceleration(0.0).move(timeDelta), None)
    } else {
      val current = events.head
      def nextSchedule = schedule.modify(_.timestamps).using(_.tail)

      current match {
        case AccelerationTimestamp(_, _, timeEnd) if currentTime > timeEnd =>
          internalMoveWithSchedule(currentTime, timeDelta, nextSchedule)
        case AccelerationTimestamp(_, timeStart, _)
            if timeStart > currentTime =>
          val duration = current.timeStart - currentTime
          val durationSeconds = seconds(duration)
          if (durationSeconds < timeDelta) {
            moveWithAcceleration(durationSeconds)
              .internalMoveWithSchedule(currentTime + duration,
                                        timeDelta - durationSeconds,
                                        schedule)
          } else {
            (moveWithAcceleration(timeDelta), Some(schedule))
          }

        case event @ AccelerationTimestamp(acc, timeStart, timeEnd)
            if timeStart == currentTime =>
          if (event.durationSeconds < timeDelta) {
            withAcceleration(acc)
              .moveWithAcceleration(event.durationSeconds)
              .internalMoveWithSchedule(currentTime + event.durationMillis,
                                        timeDelta - event.durationSeconds,
                                        nextSchedule)
          } else {
            (withAcceleration(acc).move(timeDelta), Some(schedule))
          }

        case event @ AccelerationTimestamp(acc, timeStart, timeEnd)
            if timeStart < currentTime =>
          if (acceleration isEqual acc) {
            val endOfCurrentStep = currentTime + timeDelta.seconds.toMillis

            if (endOfCurrentStep > timeEnd) {
              val duration = timeEnd - currentTime
              moveWithAcceleration(seconds(duration))
                .internalMoveWithSchedule(timeEnd,
                                          timeDelta - seconds(duration),
                                          nextSchedule)
            } else {
              (moveWithAcceleration(timeDelta), Some(schedule))
            }
          } else {
            val delayTime = currentTime - timeStart
            val fixedSchedule =
              repairSchedule(delayTime.millis, schedule.timestamps)
            internalMoveWithSchedule(currentTime, timeDelta, fixedSchedule)
          }
      }
    }
  }

  private def repairSchedule(
      deleyTime: FiniteDuration,
      remainingEvents: List[AccelerationTimestamp]): AccelerationSchedule = {
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
    iterate(Duration.Zero, remainingEvents, Nil)
  }
}
