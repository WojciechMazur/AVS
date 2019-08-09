package pl.edu.agh.wmazur.avs.model.entity.intersection.extension.policy

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import pl.edu.agh.wmazur.avs.model.entity.intersection.extension.IntersectionConnectivity
import pl.edu.agh.wmazur.avs.model.entity.intersection.{
  AutonomousIntersectionManager,
  IntersectionManager
}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.VehicleDriver.Protocol.ReservationRejected

trait DefaultPolicy {
  self: AutonomousIntersectionManager with IntersectionConnectivity =>

  lazy val defaultPolicy: Behavior[IntersectionManager.Protocol] =
    Behaviors.receiveMessagePartial {
      case req @ IntersectionManager.Protocol.IntersectionCrossingRequest(
            vin,
            _,
            _,
            timestamp,
            replyTo) =>
        context.log.info("reservation request")
        replyTo ! ReservationRejected(
          requestId = req.id,
          nextAllowedCommunicationTimestamp = timestamp,
          reason = ReservationRejected.Reason.NoClearPath,
        )

        Behaviors.same
    }
}
