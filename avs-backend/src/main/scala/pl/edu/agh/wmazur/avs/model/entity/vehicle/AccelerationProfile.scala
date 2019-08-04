package pl.edu.agh.wmazur.avs.model.entity.vehicle

import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.vehicle.AccelerationProfile.AccelerationEvent
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.Velocity
import pl.edu.agh.wmazur.avs.simulation.reservation.ReservationArray.Timestamp

import scala.concurrent.duration.FiniteDuration

case class AccelerationProfile(events: List[AccelerationEvent]) {

  def toAccelerationSchedule(initialTime: Timestamp): AccelerationSchedule = {
    val (_, accTimestamps) = events.foldLeft(
      (initialTime, List.empty[AccelerationSchedule.AccelerationTimestamp])) {
      case ((time, acc), AccelerationEvent(acceleration, duration)) => {
        val newAcc = acc :+ AccelerationSchedule.AccelerationTimestamp(
          acceleration,
          time)
        (time + duration.toMillis, newAcc)
      }
    }
    AccelerationSchedule(accTimestamps)
  }

}

object AccelerationProfile {
  case class AccelerationEvent(acceleration: Double, duration: FiniteDuration)
}
