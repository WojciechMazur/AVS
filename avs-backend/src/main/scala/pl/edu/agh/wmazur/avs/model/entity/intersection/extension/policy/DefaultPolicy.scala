package pl.edu.agh.wmazur.avs.model.entity.intersection.extension.policy

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import pl.edu.agh.wmazur.avs.model.entity.intersection.extension.IntersectionConnectivity
import pl.edu.agh.wmazur.avs.model.entity.intersection.extension.ReservationSystem.ProposalFilter.RejectedResult
import pl.edu.agh.wmazur.avs.model.entity.intersection.{
  AutonomousIntersectionManager,
  IntersectionManager
}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.VehicleDriver.Protocol.ReservationRejected
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.VehicleDriver.Protocol.ReservationRejected.Reason

trait DefaultPolicy {
  self: AutonomousIntersectionManager with IntersectionConnectivity =>

  lazy val defaultPolicy: Behavior[IntersectionManager.Protocol] =
    Behaviors.receiveMessagePartial {
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
              nextAllowedCommunicationTimestamp = timestamp,
              reason = reason,
            )

          case (_, validProposals) =>
            val optAcceptance = for {
              reservationParameters <- calcReservationParamaters(
                req,
                validProposals.map(_.proposal))
              acceptance = buildReservationAcceptance(req.id,
                                                      reservationParameters)
              _ = driverRef ! acceptance
            } yield acceptance

            if (optAcceptance.isEmpty) {
              driverRef ! ReservationRejected(
                requestId = req.id,
                nextAllowedCommunicationTimestamp = timestamp,
                reason = Reason.Dropped,
              )
            }
        }

        Behaviors.same
    }
}
