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
import pl.edu.agh.wmazur.avs.protocol.{Ack, Request, SimulationProtocol}
import pl.edu.agh.wmazur.avs.model.entity.intersection.reservation.ReservationArray.Timestamp
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
        driverRef: ActorRef[VehicleDriver.Protocol],
        spec: IntersectionCrossingRequest.CrossingVehicleSpec,
        proposals: List[IntersectionCrossingRequest.Proposal],
        currentTime: Timestamp,
        replyTo: ActorRef[VehicleDriver.Protocol]
    ) extends Request[Protocol, VehicleDriver.Protocol]
        with Protocol

    object IntersectionCrossingRequest {
      final case class Proposal(
          arrivalLaneId: Lane#Id,
          departureLane: Lane,
          arrivalTime: Long,
          arrivalVelocity: Velocity,
          maxTurnVelocity: Velocity,
          accelerationSchedule: AccelerationSchedule
      ) {
        require(maxTurnVelocity > 0.0)
      }

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
      ) {
        def toVehicleSpec(maxVelocity: Velocity): VehicleSpec =
          VehicleSpec(
            maxAcceleration = maxAcceleration,
            maxDeceleration = maxDeceleration,
            maxVelocity = maxVelocity,
            minVelocity = minVelocity,
            length = length,
            width = width,
            height = 0.meters,
            frontAxleDisplacement = frontAxleDisplacement,
            rearAxleDisplacement = rearAxleDisplacement,
            wheelRadius = 0.meters,
            wheelWidth = 0.meters,
            maxSteeringAngle = maxSteeringAngle,
            maxTurnPerSecond = maxTurnPerSecond,
            id = -1L
          )
      }

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

    final case class ReservationCompleted(
        driverRef: ActorRef[VehicleDriver.Protocol],
        reservationId: Long)
        extends Ack[Protocol](reservationId)
        with Protocol

    final case class CancelReservation(
        reservationId: Long,
        replyTo: ActorRef[VehicleDriver.Protocol])
        extends Request[Protocol, VehicleDriver.Protocol]
        with Protocol {
      val driverRef: ActorRef[VehicleDriver.Protocol] = replyTo
    }

    final case class ExitedControlZone(
        reservationId: Long,
        driverRef: ActorRef[VehicleDriver.Protocol])
        extends Request[Protocol, VehicleDriver.Protocol]
        with Protocol {
      override val replyTo: ActorRef[VehicleDriver.Protocol] = driverRef
    }

  }

  final case class GetDetailedReadings(
      replyTo: ActorRef[SimulationStateGatherer.Protocol])
      extends Protocol

}
