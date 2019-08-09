package pl.edu.agh.wmazur.avs.model.entity.vehicle.movement

import java.util.concurrent.TimeUnit

import pl.edu.agh.wmazur.avs.model.entity.vehicle.{
  AccelerationSchedule,
  Vehicle
}
import com.softwaremill.quicklens._

import scala.concurrent.duration._
import pl.edu.agh.wmazur.avs.model.entity.vehicle.movement.VehicleMovement.{
  ScheduledVehicleMovement,
  TimeDeltaSeconds
}
import pl.edu.agh.wmazur.avs.model.entity.intersection.reservation.ReservationArray.Timestamp

import scala.annotation.tailrec

trait AccelerationScheduleMovement extends ScheduledVehicleMovement {
  self: Vehicle with VelocityReachingMovement =>

  def accelerationSchedule: Option[AccelerationSchedule]
  def withAccelerationSchedule(
      accelerationSchedule: Option[AccelerationSchedule]): self.type

  def move(currentTime: Timestamp, timeDelta: TimeDeltaSeconds): self.type = {
    accelerationSchedule match {
      case Some(_) => moveWithSchedule(currentTime, timeDelta)
      case _       => move(timeDelta)
    }
  }

  override def moveWithSchedule(currentTime: Timestamp,
                                timeDelta: TimeDeltaSeconds): self.type = {
    require(accelerationSchedule.isDefined, "No acceleration schedule")

    val schedule = accelerationSchedule.get
    val (state, updatedSchedule) =
      internalMoveWithSchedule(currentTime, timeDelta, schedule)

    state.withAccelerationSchedule(updatedSchedule).asInstanceOf[self.type]
  }

  private def seconds(value: Long) = value.millis.toUnit(TimeUnit.SECONDS)

  @tailrec
  private def internalMoveWithSchedule(currentTime: Timestamp,
                                       timeDelta: TimeDeltaSeconds,
                                       schedule: AccelerationSchedule)
    : (self.type, Option[AccelerationSchedule]) = {
    val events = schedule.timestamps

    if (schedule.timestamps.isEmpty) {
      (self.move(timeDelta), Some(schedule))
    } else {
      val next = events.head
      if (next.timeStart > currentTime) { //to early
        val duration = next.timeStart - currentTime
        val durationSeconds = seconds(duration)
        if (duration < timeDelta) {
          move(durationSeconds)
            .internalMoveWithSchedule(currentTime + duration,
                                      timeDelta - durationSeconds,
                                      schedule)
        } else {
          (move(timeDelta), Some(schedule))
        }
      } else if (next.timeStart == currentTime) {
        val newSchedule = schedule.modify(_.timestamps).using(_.tail)
        val withAppliedAcceleration = withAcceleration(next.acceleration)
        if (newSchedule.timestamps.nonEmpty) {
          val next = newSchedule.timestamps.head
          val duration = next.timeStart - currentTime
          val durationSeconds = seconds(duration)
          if (durationSeconds < timeDelta) {
            withAppliedAcceleration
              .move(durationSeconds)
              .internalMoveWithSchedule(currentTime + duration,
                                        timeDelta - durationSeconds,
                                        newSchedule)
          } else {
            (move(timeDelta), Some(newSchedule))
          }
        } else {
          (move(timeDelta), None)
        }
      } else {
        val newSchedule = schedule.modify(_.timestamps).using(_.tail)
        internalMoveWithSchedule(currentTime, timeDelta, newSchedule)
      }
    }

  }
}
