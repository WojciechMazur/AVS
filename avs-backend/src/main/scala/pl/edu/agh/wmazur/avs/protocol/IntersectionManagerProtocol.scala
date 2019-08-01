package pl.edu.agh.wmazur.avs.protocol

import akka.actor.typed.ActorRef
import pl.edu.agh.wmazur.avs.model.entity.road.Lane
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.Velocity
import pl.edu.agh.wmazur.avs.protocol.IntersectionManagerProtocol.ReservationConfirmed.AccelerationProfile
import pl.edu.agh.wmazur.avs.simulation.reservation.ReservationArray.Timestamp

import scala.concurrent.duration.FiniteDuration

sealed trait IntersectionManagerProtocol
object IntersectionManagerProtocol {

  final case class ReservationConfirmed(
      reservationId: Int,
      requestId: Int,
      arrivalTime: Long,
      safetyBufferBefore: FiniteDuration,
      safetyBufferAfter: FiniteDuration,
      arrivalVelocity: Velocity,
      arrivalLaneId: Lane#Id,
      departureLaneId: Lane#Id,
      accelerationProfile: AccelerationProfile,
  ) extends Response[IntersectionManagerProtocol](requestId)
      with IntersectionManagerProtocol

  object ReservationConfirmed {
    case class AccelerationProfile(events: List[AccelerationEvent])
    case class AccelerationEvent(acceleration: Double, duration: FiniteDuration)
  }

  final case class ReservationRejected(
      requestId: Long,
      nextAllowedCommunicationTimestamp: Timestamp,
      reason: ReservationRejected.Reason,
  ) extends Response[IntersectionManagerProtocol](requestId)
      with IntersectionManagerProtocol

  object ReservationRejected {
    sealed trait Reason
    object Reason {
      case object NoClearPath extends Reason
      case object ConfirmedAnotherRequest extends Reason
      case object ArrivalTimeTooLate extends Reason
      case object ArrivalTimeTooEarly extends Reason
      case object TooEarly extends Reason
      case object Dropped extends Reason
    }
  }

}
