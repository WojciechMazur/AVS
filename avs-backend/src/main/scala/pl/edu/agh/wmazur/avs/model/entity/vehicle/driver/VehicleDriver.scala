package pl.edu.agh.wmazur.avs.model.entity.vehicle.driver

import akka.actor.typed.ActorRef
import mikera.vectorz.Vector2
import org.locationtech.jts.geom.Geometry
import org.locationtech.spatial4j.shape.Point
import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.intersection.IntersectionManager
import pl.edu.agh.wmazur.avs.model.entity.road.{Lane, Road}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.Velocity
import pl.edu.agh.wmazur.avs.model.entity.vehicle.{
  AccelerationProfile,
  BasicVehicle,
  Vehicle
}
import pl.edu.agh.wmazur.avs.protocol.{Response, SimulationProtocol}
import pl.edu.agh.wmazur.avs.model.entity.intersection.reservation.ReservationArray.{
  ReservationId,
  Timestamp
}

import scala.collection.mutable
import scala.concurrent.duration.FiniteDuration

/**
  * Responsible for controlling vehicle and communication with intersection managers
  */
trait VehicleDriver {
  var vehicle: BasicVehicle
  var currentLane: Lane

  var driverGauges: VehicleDriverGauges = VehicleDriverGauges.empty
  def occupiedLanes: mutable.Set[Lane]

  def destination: Option[Road]

  def prepareToMove(): VehicleDriver = {
    updateGauges
  }

  def updateGauges: this.type = {
    driverGauges = driverGauges
      .updateDistanceToIntersections(nextIntersectionPosition,
                                     previousIntersectionPosition,
                                     vehicle)

    this
  }

  final def updateVehicle(fn: Vehicle => Vehicle): VehicleDriver = {
    withVehicle(fn(vehicle))
  }
  def withVehicle(vehicle: Vehicle): this.type

  var nextIntersectionManager: Option[ActorRef[IntersectionManager.Protocol]] =
    None
  var prevIntersectionManager: Option[ActorRef[IntersectionManager.Protocol]] =
    None

  var nextIntersectionPosition: Option[Point] = None
  var previousIntersectionPosition: Option[Point] = None

  def distanceToPoint(point: Point): Dimension = {
    val here = Vector2.of(vehicle.position.getX, vehicle.position.getY)
    val there = Vector2.of(point.getX, point.getY)
    here.distance(there).geoDegrees
  }

  protected def setCurrentLane(lane: Lane): Lane = {
    currentLane = lane
    occupiedLanes.clear()
    occupiedLanes.update(lane, included = true)
    lane
  }
}

object VehicleDriver {
  trait Protocol extends SimulationProtocol
  object Protocol {

    final case class ReservationConfirmed(
        reservationId: ReservationId,
        requestId: Long,
        reservationDetails: ReservationDetails
    ) extends Response[Protocol](requestId)
        with Protocol

    case class ReservationDetails(
        reservationId: ReservationId,
        admissionId: ReservationId,
        intersectionManagerRef: ActorRef[IntersectionManager.Protocol],
        arrivalTime: Long,
        safetyBufferBefore: FiniteDuration,
        safetyBufferAfter: FiniteDuration,
        arrivalVelocity: Velocity,
        arrivalLaneId: Lane#Id,
        departureLaneId: Lane#Id,
        accelerationProfile: AccelerationProfile
    )

    final case class ReservationRejected(
        requestId: Long,
        nextAllowedCommunicationTimestamp: Timestamp,
        reason: ReservationRejected.Reason,
    ) extends Response[Protocol](requestId)
        with Protocol

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

    final case class IntersectionManagerInRange(
        ref: ActorRef[IntersectionManager.Protocol],
        position: Point,
        geometry: Geometry)
        extends Protocol
  }

}
