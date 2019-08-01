package pl.edu.agh.wmazur.avs.model.entity.intersection.policy

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import pl.edu.agh.wmazur.avs.model.entity.intersection.Vehicle2IntersectionManager
import pl.edu.agh.wmazur.avs.protocol.IntersectionManagerProtocol.ReservationRejected
import pl.edu.agh.wmazur.avs.protocol.{
  IntersectionManagerProtocol,
  DriverProtocol
}
import pl.edu.agh.wmazur.avs.protocol.DriverProtocol.IntersectionCrossingRequest

trait ClosedIntersectionPolicy {
  self: Vehicle2IntersectionManager =>

  val closedIntersection: Behavior[DriverProtocol] =
    Behaviors.receiveMessage {
      case req @ IntersectionCrossingRequest(_, _, _, _, timestamp, replyTo) =>
        replyTo ! IntersectionManagerProtocol.ReservationRejected(
          requestId = req.id,
          nextAllowedCommunicationTimestamp = timestamp,
          reason = ReservationRejected.Reason.NoClearPath,
        )

        Behaviors.same
    }
}
