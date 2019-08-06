package pl.edu.agh.wmazur.avs.model.entity.intersection.policy

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import pl.edu.agh.wmazur.avs.model.entity.intersection.workers.IntersectionCoordinator
import pl.edu.agh.wmazur.avs.model.entity.intersection.{
  AutonomousIntersectionManager,
  IntersectionManager
}

trait Routing {
  self: AutonomousIntersectionManager =>

  val routing: Behavior[IntersectionManager.Protocol] =
    Behaviors.receiveMessagePartial {
      case msg: IntersectionManager.Protocol =>
        msg match {
          case msg: IntersectionCoordinator.Protocol =>
            workers.coordinator ! msg
          case protocol =>
            context.log.warning(
              "AutonomousIntersectionManager :: Routing :: Unknown protocol: {}",
              protocol)
        }
        Behaviors.same
    }
}
