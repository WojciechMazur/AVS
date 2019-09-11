package pl.edu.agh.wmazur.avs.model.entity.intersection.extension.policy

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import pl.edu.agh.wmazur.avs.model.entity.intersection.IntersectionManager.Protocol.ExitedControlZone
import pl.edu.agh.wmazur.avs.model.entity.intersection.extension.IntersectionConnectivity
import pl.edu.agh.wmazur.avs.model.entity.intersection.extension.ReservationSystem.ProposalFilter.RejectedResult
import pl.edu.agh.wmazur.avs.model.entity.intersection.{
  AutonomousIntersectionManager,
  IntersectionManager
}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.VehicleDriver.Protocol.ReservationRejected
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.VehicleDriver.Protocol.ReservationRejected.Reason
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.VehicleDriver.Protocol.ReservationRejected.Reason.NoClearPath
import pl.edu.agh.wmazur.avs.simulation.TickSource

import scala.util.Random

trait DefaultPolicy {
  self: AutonomousIntersectionManager with IntersectionConnectivity =>

  object NextAllowedCommunicationInterval {
    val emptyProposals = 250
    val noValidProposal = 500
    def noAcceptance = 500 + Random.nextInt(500)

  }

  lazy val firstComeFirstServed: Behavior[IntersectionManager.Protocol] =
    Behaviors.receiveMessagePartial {
      case req: IntersectionManager.Protocol.IntersectionCrossingRequest
          if req.proposals.isEmpty =>
        context.log.warning("Received list of empty proposals")
        req.driverRef ! ReservationRejected(
          requestId = req.id,
          nextAllowedCommunicationTimestamp = req.currentTime + NextAllowedCommunicationInterval.emptyProposals,
          reason = NoClearPath,
        )
        Behaviors.same

      case req @ IntersectionManager.Protocol.IntersectionCrossingRequest(
            driverRef,
            _,
            proposals,
            timestamp,
            replyTo) =>
        proposals
          .map(standardProposalValidity(_))
          .partition(_.isRejected) match {
          case (rejectedProposals, validProposals) if validProposals.isEmpty =>
            val reason = rejectedProposals.head
//              .getOrElse(Reason.NoClearPath)
              .asInstanceOf[RejectedResult]
              .resaon

            replyTo ! ReservationRejected(
              requestId = req.id,
              nextAllowedCommunicationTimestamp = timestamp + NextAllowedCommunicationInterval.noValidProposal,
              reason = reason,
            )
            context.log.warning(
              s"Request {} rejected, reason {}. No valid proposal Arrival lanes {}, Departur lanes {}",
              req.id,
              reason,
              req.proposals
                .map(_.arrivalLaneId)
                .map(id =>
                  id -> intersection.lanesById(id).spec.turningAllowance)
                .mkString(", "),
              req.proposals.map(_.departureLane.id).mkString(", ")
            )

          case (_, validProposals) =>
            val optAcceptance = for {
              reservationParameters <- calcReservationParamaters(
                req,
                validProposals.map(_.proposal))
              acceptance <- buildReservationAcceptance(req.id,
                                                       reservationParameters)
              _ = {
                driverRef ! acceptance
                context.unwatch(driverRef)
                context.watchWith(
                  driverRef,
                  ExitedControlZone(acceptance.reservationId, driverRef))
                context.log.debug(
                  s"Request {} confirmed, arrival lane {}, departure lane {}",
                  req.id,
                  acceptance.reservationDetails.arrivalLaneId,
                  acceptance.reservationDetails.departureLaneId
                )
              }
            } yield acceptance

            if (optAcceptance.isEmpty) {
              val reason = Reason.NoClearPath

              context.log.warning(
                s"Request {} rejected, reason {}. No acceptance. Arrival lanes {}, Departure lanes {}",
                req.id,
                reason,
                req.proposals
                  .map(_.arrivalLaneId)
                  .map(id =>
                    id -> intersection.lanesById(id).spec.turningAllowance)
                  .mkString(", "),
                req.proposals.map(_.departureLane.id).mkString(", ")
              )
              driverRef ! ReservationRejected(
                requestId = req.id,
                nextAllowedCommunicationTimestamp = timestamp + NextAllowedCommunicationInterval.noAcceptance,
                reason = reason,
              )
            }
        }

        Behaviors.same
    }
}
