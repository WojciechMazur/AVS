package pl.edu.agh.wmazur.avs.model.entity.intersection.policy

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import pl.edu.agh.wmazur.avs.model.entity.intersection.{
  AutonomousIntersectionManager,
  IntersectionManager
}

trait DefaultPolicy {
  self: AutonomousIntersectionManager with IntersectionConnectivity =>

  lazy val defaultPolicy: Behavior[IntersectionManager.Protocol] =
    Behaviors.empty.orElse(basicConnectivity)
}
