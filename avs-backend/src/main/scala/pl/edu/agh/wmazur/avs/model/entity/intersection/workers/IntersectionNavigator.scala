package pl.edu.agh.wmazur.avs.model.entity.intersection.workers

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import pl.edu.agh.wmazur.avs.Agent
import pl.edu.agh.wmazur.avs.model.entity.intersection.{
  AutonomousRoadIntersection,
  IntersectionManager
}
import pl.edu.agh.wmazur.avs.model.entity.road.{Lane, Road}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousVehicleDriver

class IntersectionNavigator(
    val context: ActorContext[IntersectionNavigator.Protocol],
    intersectionManagerRef: ActorRef[IntersectionManager.Protocol],
    intersection: AutonomousRoadIntersection)
    extends Agent[IntersectionNavigator.Protocol] {

  override protected val initialBehaviour
    : Behavior[IntersectionNavigator.Protocol] = Behaviors.empty
}

object IntersectionNavigator {
  sealed trait NavigatorProtocol

  sealed trait Protocol
      extends IntersectionManager.Protocol
      with NavigatorProtocol

  object Protocol {
    case class FindPath(
        replyTo: ActorRef[AutonomousVehicleDriver.Protocol],
        currentLane: Lane#Id,
        destinationRoadId: Option[Road#Id] = None,
    ) extends Protocol

  }
}
