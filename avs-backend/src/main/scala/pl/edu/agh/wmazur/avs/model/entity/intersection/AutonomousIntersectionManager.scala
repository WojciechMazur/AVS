package pl.edu.agh.wmazur.avs.model.entity.intersection

import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.{ActorContext, Behaviors, TimerScheduler}
import akka.actor.typed.{ActorRef, Behavior}
import pl.edu.agh.wmazur.avs.model.entity.intersection.AutonomousIntersectionManager.Protocol.FetchDrivers
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
    val roads: List[Road],
    val context: ActorContext[IntersectionManager.Protocol],
    val timers: TimerScheduler[IntersectionManager.Protocol]
) extends Agent[IntersectionManager.Protocol]
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
  sealed trait Protocol extends IntersectionManager.Protocol
  object Protocol {
    case object FetchDrivers extends Protocol
  }

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

      Behaviors.withTimers { timers =>
        timers.startPeriodicTimer(FetchDrivers,
                                  FetchDrivers,
                                  transmitionInterval)

        new AutonomousIntersectionManager(intersection = intersection,
                                          gridManagerConfig = managerConfig,
                                          roads = roads,
                                          context = ctx,
                                          timers = timers)
      }
    }
  val maximumFutureReservationTime: FiniteDuration = 10.seconds
  val defaultACZSize: Dimension = 40.meters

  val ACZDistanceShapeLength: Dimension = 1.meters
  val transmitionInterval: FiniteDuration = 1.seconds

  val maxTransmitionDistance: Dimension = 200.meters
}
