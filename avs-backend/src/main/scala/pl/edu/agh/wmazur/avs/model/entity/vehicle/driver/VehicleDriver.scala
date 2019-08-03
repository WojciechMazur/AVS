package pl.edu.agh.wmazur.avs.model.entity.vehicle.driver

import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.intersection.IntersectionManager
import pl.edu.agh.wmazur.avs.model.entity.road.{Lane, Road}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.Vehicle
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.Velocity
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.VehicleDriver.Protocol.ReservationConfirmed.AccelerationProfile
import pl.edu.agh.wmazur.avs.protocol.{Response, SimulationProtocol}
import pl.edu.agh.wmazur.avs.simulation.reservation.ReservationArray.Timestamp

import scala.collection.mutable
import scala.concurrent.duration.FiniteDuration

/**
  * Responsible for controlling vehicle and communication with intersection managers
  */
trait VehicleDriver {
  def vehicle: Vehicle
  var currentLane: Lane
  def occupiedLanes: mutable.Set[Lane]

//  def spawnPoint: Option[SpawnPoint]
  def destination: Option[Road]
  def prepareToMove(): VehicleDriver = {
    this
  }
  final def updateVehicle(fn: Vehicle => Vehicle): VehicleDriver = {
    withVehicle(fn(vehicle))
  }
  protected def withVehicle(vehicle: Vehicle): VehicleDriver

  def nextIntersectionManager: Option[IntersectionManager]
  def prevIntersectionManager: Option[IntersectionManager]

  def distanceToNextIntersection: Option[Dimension]
  def distanceToPrevIntersection: Option[Dimension]

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
        reservationId: Int,
        requestId: Int,
        arrivalTime: Long,
        safetyBufferBefore: FiniteDuration,
        safetyBufferAfter: FiniteDuration,
        arrivalVelocity: Velocity,
        arrivalLaneId: Lane#Id,
        departureLaneId: Lane#Id,
        accelerationProfile: AccelerationProfile,
    ) extends Response[Protocol](requestId)
        with Protocol

    object ReservationConfirmed {
      case class AccelerationProfile(events: List[AccelerationEvent])
      case class AccelerationEvent(acceleration: Double,
                                   duration: FiniteDuration)
    }

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

  }

}
