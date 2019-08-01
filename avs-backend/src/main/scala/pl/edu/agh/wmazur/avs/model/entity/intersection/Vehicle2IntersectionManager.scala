package pl.edu.agh.wmazur.avs.model.entity.intersection

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import pl.edu.agh.wmazur.avs.{Agent, Dimension}
import pl.edu.agh.wmazur.avs.model.entity.intersection.policy.{
  ClosedIntersectionPolicy,
  DefaultPolicy
}
import pl.edu.agh.wmazur.avs.protocol.{SimulationProtocol, DriverProtocol}
import pl.edu.agh.wmazur.avs.simulation.reservation.GridReservationManager

import scala.concurrent.duration._

class Vehicle2IntersectionManager(
    val intersection: Intersection,
    gridManagerConfig: GridReservationManager.ManagerConfig,
    val context: ActorContext[DriverProtocol])
    extends Agent[DriverProtocol]
    with IntersectionManager
    with ClosedIntersectionPolicy
    with DefaultPolicy {
  // TODO ACZs
  val reservationManager =
    GridReservationManager(gridManagerConfig, intersection)

  override protected val initialBehaviour: Behavior[DriverProtocol] =
    closedIntersection
}

object Vehicle2IntersectionManager {
  def apply(intersection: Intersection,
            managerConfig: GridReservationManager.ManagerConfig)
    : Behavior[DriverProtocol] =
    Behaviors.setup { ctx =>
      new Vehicle2IntersectionManager(intersection = intersection,
                                      gridManagerConfig = managerConfig,
                                      context = ctx)
    }

  val maximumFutureReservationTime: FiniteDuration = 10.seconds
  val defaultACZSize: Dimension = 40.meters
  val ACZDistanceShapeLength: Dimension = 1.meters
}
