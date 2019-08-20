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

  var currentPath: List[Road] = Nil
  var hasPlannedPath = false

  def nextRoad: Road = currentPath.tail.head

  val navigation: Behavior[AutonomousVehicleDriver.Protocol] =
    Behaviors.receiveMessagePartial {
      case PathToFollow(roads, lanes)
          if currentPath.headOption.forall(_.id == lanes.head.road.id) =>
        currentPath = roads
        hasPlannedPath = true
        if (nextIntersectionManager.isDefined) {
          println("Tring to send request (nav)")
          context.self ! TrySendReservationRequest
        }
        Behaviors.same
      case NoPathFound =>
        println("retrying to get path")
        navigatorRef ! GlobalNavigator.Protocol.FindPath(
          context.self,
          Right(currentLane),
          destination.map(Left.apply))
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
