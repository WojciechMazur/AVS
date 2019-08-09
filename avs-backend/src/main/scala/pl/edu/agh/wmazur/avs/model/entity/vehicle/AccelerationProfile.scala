package pl.edu.agh.wmazur.avs.model.entity.vehicle

import pl.edu.agh.wmazur.avs.model.entity.vehicle.AccelerationProfile.AccelerationEvent
import pl.edu.agh.wmazur.avs.model.entity.intersection.reservation.ReservationArray.Timestamp

import scala.concurrent.duration.FiniteDuration

case class AccelerationProfile(events: List[AccelerationEvent]) {

  def toAccelerationSchedule(initialTime: Timestamp): AccelerationSchedule = {
    val (_, accTimestamps) = events.foldLeft(
      (initialTime, List.empty[AccelerationSchedule.AccelerationTimestamp])) {
      case ((time, acc), AccelerationEvent(acceleration, duration)) =>
        val endTime = time + duration.toMillis
        val newAcc = acc :+ AccelerationSchedule.AccelerationTimestamp(
          acceleration = acceleration,
          timeStart = time,
          timeEnd = endTime)

        (endTime, newAcc)
    }
    AccelerationSchedule(accTimestamps)
  }

}

object AccelerationProfile {
  case class AccelerationEvent(acceleration: Double, duration: FiniteDuration)

  def apply(accelerationEvents: AccelerationEvent*): AccelerationProfile =
    new AccelerationProfile(accelerationEvents.toList)
}
