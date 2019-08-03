package pl.edu.agh.wmazur.avs.model.entity.intersection.policy

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import pl.edu.agh.wmazur.avs.model.entity.intersection.{
  AutonomousIntersectionManager,
  IntersectionManager
}
import pl.edu.agh.wmazur.avs.model.entity.intersection.IntersectionManager.Protocol._
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.VehicleDriver.Protocol.ReservationRejected

trait ClosedIntersectionPolicy {
  self: AutonomousIntersectionManager with IntersectionConnectivity =>

  lazy val closedIntersection: Behavior[IntersectionManager.Protocol] =
    Behaviors
      .receiveMessagePartial[IntersectionManager.Protocol] {
        case req @ IntersectionCrossingRequest(_,
                                               _,
                                               _,
                                               _,
                                               timestamp,
                                               replyTo) =>
          replyTo ! ReservationRejected(
            requestId = req.id,
            nextAllowedCommunicationTimestamp = timestamp,
            reason = ReservationRejected.Reason.NoClearPath,
          )

          Behaviors.same
      }
      .orElse(basicConnectivity)
}
