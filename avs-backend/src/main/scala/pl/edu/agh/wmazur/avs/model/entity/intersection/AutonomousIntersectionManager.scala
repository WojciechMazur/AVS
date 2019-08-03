package pl.edu.agh.wmazur.avs.model.entity.intersection

import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import pl.edu.agh.wmazur.avs
import pl.edu.agh.wmazur.avs.model.entity.intersection.policy.{
  ClosedIntersectionPolicy,
  DefaultPolicy,
  IntersectionConnectivity
}
import pl.edu.agh.wmazur.avs.model.entity.road.Road
import pl.edu.agh.wmazur.avs.simulation.EntityManager.SpawnResult
import pl.edu.agh.wmazur.avs.simulation.reservation.GridReservationManager
import pl.edu.agh.wmazur.avs.{Agent, Dimension, EntityRefsGroup}

import scala.concurrent.duration._

class AutonomousIntersectionManager(
    val intersection: Intersection,
    gridManagerConfig: GridReservationManager.ManagerConfig,
    val context: ActorContext[IntersectionManager.Protocol])
    extends Agent[IntersectionManager.Protocol]
    with IntersectionManager
    with ClosedIntersectionPolicy
    with DefaultPolicy
    with IntersectionConnectivity {
  // TODO ACZs
  val reservationManager =
    GridReservationManager(gridManagerConfig, intersection)

  override protected val initialBehaviour
    : Behavior[IntersectionManager.Protocol] =
    closedIntersection
}

object AutonomousIntersectionManager {
  //scalastyle:off
  def init(
      optId: Option[Intersection#Id],
      roads: List[Road],
      managerConfig: GridReservationManager.ManagerConfig,
      replyTo: Option[
        ActorRef[SpawnResult[Intersection, IntersectionManager.Protocol]]] =
        None
  ): Behavior[IntersectionManager.Protocol] =
    //scalastyle:on
    Behaviors.setup { ctx =>
      val id = optId.getOrElse(Intersection.nextId)
      val intersection = AutonomousRoadIntersection(id, roads, ctx.self)
      replyTo.foreach(_ ! SpawnResult(intersection, ctx.self))

      ctx.system.receptionist ! Receptionist.register(
        EntityRefsGroup.intersection,
        ctx.self)
      new AutonomousIntersectionManager(intersection = intersection,
                                        gridManagerConfig = managerConfig,
                                        context = ctx)
    }

  val maximumFutureReservationTime: FiniteDuration = 10.seconds
  val defaultACZSize: Dimension = 40.meters
  val ACZDistanceShapeLength: Dimension = 1.meters

  sealed trait Protocol extends IntersectionManager.Protocol
}
