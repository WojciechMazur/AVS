package pl.edu.agh.wmazur.avs.model.entity.intersection.policy

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import pl.edu.agh.wmazur.avs.model.entity.intersection.{
  AutonomousIntersectionManager,
  IntersectionManager
}
import pl.edu.agh.wmazur.avs.simulation.stage.SimulationStateGatherer

trait IntersectionConnectivity {
  self: AutonomousIntersectionManager =>
  import IntersectionManager._
  lazy val basicConnectivity: Behavior[IntersectionManager.Protocol] =
    Behaviors.receiveMessagePartial {
      case GetDetailedReadings(replyTo) =>
        replyTo ! SimulationStateGatherer.IntersectionDetailedReading(
          context.self,
          intersection)

        Behaviors.same
    }
}
