package pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.protocol

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import pl.edu.agh.wmazur.avs.model.entity.intersection.workers.GlobalNavigator
import pl.edu.agh.wmazur.avs.model.entity.road.{Lane, Road}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousVehicleDriver
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousVehicleDriver.ExtendedProtocol

trait Navigation {
  self: AutonomousVehicleDriver =>
  import AutonomousVehicleDriver._

  var currentPath: List[Lane] = Nil
  var hasPlannedPath = false

  def nextLane: Lane = currentPath.tail.head

  val navigation: Behavior[AutonomousVehicleDriver.Protocol] =
    Behaviors.receiveMessagePartial {
      case PathToFollow(roads, lanes)
          if currentPath.headOption.forall(_.id == lanes.head.road.id) =>
        println(lanes.map(_.id))
        currentPath = lanes
        hasPlannedPath = true
        destination = roads.lastOption.map(_.id)
        if (nextIntersectionManager.isDefined) {
          context.self ! AskForMaximalCrossingVelocities
        }
        Behaviors.same
      case NoPathFound =>
        navigatorRef ! GlobalNavigator.Protocol.FindPath(context.self,
                                                         Right(currentLane),
                                                         None)
        Behaviors.same

      case NoPathForLanes(_, _) =>
        Behaviors.same
    }

}

object Navigation {
  trait Protocol {

    case class PathToFollow(roads: List[Road], lanes: List[Lane])
        extends ExtendedProtocol
    case object NoPathFound extends ExtendedProtocol
  }
}
