package pl.edu.agh.wmazur.avs.protocol

import akka.actor.typed.ActorRef
import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.intersection.Intersection
import pl.edu.agh.wmazur.avs.model.entity.road.Lane
import pl.edu.agh.wmazur.avs.model.entity.vehicle.{Vehicle, VehicleSpec}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{
  Acceleration,
  Angle,
  Velocity
}
import pl.edu.agh.wmazur.avs.simulation.reservation.ReservationArray.Timestamp

sealed trait DriverProtocol extends SimulationProtocol
object DriverProtocol {
  final case class IntersectionCrossingRequest(
      vin: Vehicle#Id,
      intersectionId: Intersection#Id,
      spec: IntersectionCrossingRequest.VehicleSpec,
      proposals: List[IntersectionCrossingRequest.Proposal],
      currentTime: Timestamp,
      replyTo: ActorRef[IntersectionManagerProtocol]
  ) extends Command[DriverProtocol, IntersectionManagerProtocol]
      with DriverProtocol

  object IntersectionCrossingRequest {
    final case class Proposal(
        arrivalLaneId: Lane#Id,
        departureLaneId: Lane#Id,
        arrivalTime: Long,
        arrivalVelocity: Velocity,
        maxTurnVelocity: Velocity
    )

    final case class VehicleSpec(
        maxAcceleration: Acceleration,
        maxDeceleration: Acceleration,
        minVelocity: Velocity,
        length: Dimension,
        width: Dimension,
        frontAxleDisplacement: Dimension,
        rearAxleDisplacement: Dimension,
        maxSteeringAngle: Angle,
        maxTurnPerSecond: Angle
    )

    object VehicleSpec {
      def apply(vehicleSpec: VehicleSpec): VehicleSpec = {
        VehicleSpec(
          maxAcceleration = vehicleSpec.maxAcceleration,
          maxDeceleration = vehicleSpec.maxDeceleration,
          minVelocity = vehicleSpec.minVelocity,
          length = vehicleSpec.length,
          width = vehicleSpec.width,
          frontAxleDisplacement = vehicleSpec.frontAxleDisplacement,
          rearAxleDisplacement = vehicleSpec.rearAxleDisplacement,
          maxSteeringAngle = vehicleSpec.maxSteeringAngle,
          maxTurnPerSecond = vehicleSpec.maxTurnPerSecond,
        )
      }
    }
  }

  final case class ReservationCompleted(reservationId: Int)
      extends Ack[DriverProtocol](reservationId)
      with DriverProtocol

  final case class CancelReservation(
      reservationId: Int,
      replyTo: ActorRef[IntersectionManagerProtocol])
      extends Command[DriverProtocol, IntersectionManagerProtocol]
      with DriverProtocol

  final case class ExitedControlZone(
      vin: Vehicle.Vin,
      replyTo: ActorRef[IntersectionManagerProtocol])
      extends Command[DriverProtocol, IntersectionManagerProtocol]
      with DriverProtocol

}
