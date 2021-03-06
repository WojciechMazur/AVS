package pl.edu.agh.wmazur.avs.model.entity.intersection.extension.policy

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import pl.edu.agh.wmazur.avs.model.entity.intersection.extension.IntersectionConnectivity
import pl.edu.agh.wmazur.avs.model.entity.intersection.{
  AutonomousIntersectionManager,
  IntersectionManager
}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.VehicleDriver.Protocol.ReservationRejected

trait ClosedIntersectionPolicy {
  self: AutonomousIntersectionManager with IntersectionConnectivity =>

  lazy val closedIntersection: Behavior[IntersectionManager.Protocol] =
    Behaviors
      .receiveMessagePartial[IntersectionManager.Protocol] {
        case req @ IntersectionManager.Protocol.IntersectionCrossingRequest(
              _,
              _,
              _,
              timestamp,
              replyTo) =>
          context.log.warning(
            "Reservation request to closed intersection rejected")
          replyTo ! ReservationRejected(
            requestId = req.id,
            nextAllowedCommunicationTimestamp = timestamp,
            reason = ReservationRejected.Reason.NoClearPath,
          )

          Behaviors.same
      }
}
