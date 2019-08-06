package pl.edu.agh.wmazur.avs.model.entity.intersection

import akka.actor.typed.ActorRef
import org.locationtech.spatial4j.shape.Shape
import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.road.{Lane, Road}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.{
  AccelerationSchedule,
  Vehicle,
  VehicleSpec
}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{
  Acceleration,
  Angle,
  Velocity
}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.VehicleDriver
import pl.edu.agh.wmazur.avs.protocol.{Ack, Command, SimulationProtocol}
import pl.edu.agh.wmazur.avs.simulation.reservation.ReservationArray.Timestamp
import pl.edu.agh.wmazur.avs.simulation.stage.SimulationStateGatherer

trait IntersectionManager {
  def intersection: Intersection

  def managesLane(laneId: Lane#Id): Boolean =
    intersection.roads
      .exists(
        _.lanes
          .exists(_.id == laneId))

  def managesRoad(roadId: Road#Id): Boolean =
    intersection.roads
      .exists(_.id == roadId)

  def contains(vehicle: Vehicle): Boolean = intersects(vehicle.area)

  def intersects(shape: Shape): Boolean =
    intersection.area
      .relate(shape)
      .intersects()

}

object IntersectionManager {
  trait Protocol extends SimulationProtocol

  object Protocol {
    final case class IntersectionCrossingRequest(
        vin: Vehicle#Id,
        spec: IntersectionCrossingRequest.CrossingVehicleSpec,
        proposals: List[IntersectionCrossingRequest.Proposal],
        currentTime: Timestamp,
        replyTo: ActorRef[VehicleDriver.Protocol]
    ) extends Command[Protocol, VehicleDriver.Protocol]
        with Protocol

    object IntersectionCrossingRequest {
      final case class Proposal(
          arrivalLaneId: Lane#Id,
          departureLaneId: Lane#Id,
          arrivalTime: Long,
          arrivalVelocity: Velocity,
          maxTurnVelocity: Velocity,
          accelerationSchedule: AccelerationSchedule
      )

      final case class CrossingVehicleSpec(
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

      object CrossingVehicleSpec {
        def apply(vehicleSpec: VehicleSpec): CrossingVehicleSpec = {
          CrossingVehicleSpec(
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
        extends Ack[Protocol](reservationId)
        with Protocol

    final case class CancelReservation(
        reservationId: Int,
        replyTo: ActorRef[VehicleDriver.Protocol])
        extends Command[Protocol, VehicleDriver.Protocol]
        with Protocol

    final case class ExitedControlZone(
        vin: Vehicle.Vin,
        replyTo: ActorRef[VehicleDriver.Protocol])
        extends Command[Protocol, VehicleDriver.Protocol]
        with Protocol

  }

  final case class GetDetailedReadings(
      replyTo: ActorRef[SimulationStateGatherer.Protocol])
      extends Protocol

}
