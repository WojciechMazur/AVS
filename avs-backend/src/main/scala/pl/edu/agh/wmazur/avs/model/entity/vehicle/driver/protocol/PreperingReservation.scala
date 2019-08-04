package pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.protocol

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import pl.edu.agh.wmazur.avs.model.entity.intersection.IntersectionManager
import pl.edu.agh.wmazur.avs.model.entity.intersection.IntersectionManager.Protocol.IntersectionCrossingRequest
import pl.edu.agh.wmazur.avs.model.entity.road.{Lane, Road}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.Velocity
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousVehicleDriver
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousVehicleDriver.ExtendedProtocol
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.VehicleDriver.Protocol.ReservationConfirmed.AccelerationProfile
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.protocol.PreperingReservation.ArrivalEstimation
import pl.edu.agh.wmazur.avs.simulation.reservation.ReservationArray.Timestamp

import scala.collection.mutable

trait PreperingReservation {
  self: AutonomousVehicleDriver with Driving =>
  import AutonomousVehicleDriver._

  val maxAllowedVelocities
    : mutable.Map[ActorRef[IntersectionManager.Protocol], Map[Lane, Velocity]] =
    mutable.Map.empty

  def reservation: Behavior[Protocol] = Behaviors.receiveMessagePartial {
    case TryGetIntersectionReservation =>
      if (nextIntersectionManager.nonEmpty) {
        val spec =
          IntersectionCrossingRequest.CrossingVehicleSpec(vehicle.spec)

        val proposals = buildIntersectionCrossingProposals()

        val reservationRequest: IntersectionCrossingRequest =
          IntersectionManager.Protocol.IntersectionCrossingRequest(
            vin = vehicle.id,
            spec = spec,
            proposals = proposals,
            currentTime = currentTime,
            replyTo = context.self
          )
        nextIntersectionManager.get ! reservationRequest
      }
      Behaviors.same
  }

  def estimateArrival(maxArrivalVelocity: Velocity): ArrivalEstimation = {}

  private def estimateArrivalWithAccelerationProfile(
      maxArrivalVelocity: Velocity): ArrivalEstimation = {
    val accelerationProfile = this.accelerationSchedule.get
  }

  private def buildIntersectionCrossingProposals()
    : List[IntersectionCrossingRequest.Proposal] = {
    IntersectionCrossingRequest.Proposal(arrivalLaneId = currentLane.id,
                                         departureLaneId = currentLane.id,
                                         arrivalTime = 0,
                                         arrivalVelocity = 0,
                                         maxTurnVelocity = 0) :: Nil
  }
}

object PreperingReservation {
  case class ArrivalEstimation(arrivalTime: Timestamp,
                               arrivalVelocity: Velocity,
                               accelerationProfile: AccelerationProfile)

  trait Protocol {
    case class InvalidProposalParameters(laneId: Lane#Id, roadId: Road#Id)
        extends ExtendedProtocol
  }
}
