package pl.edu.agh.wmazur.avs.model.entity.intersection

import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.{ActorContext, Behaviors, TimerScheduler}
import akka.actor.typed.{ActorRef, Behavior}
import pl.edu.agh.wmazur.avs.model.entity.intersection.AutonomousIntersectionManager.Workers
import pl.edu.agh.wmazur.avs.model.entity.intersection.extension.policy.{
  ClosedIntersectionPolicy,
  DefaultPolicy
}
import pl.edu.agh.wmazur.avs.model.entity.intersection.extension.{
  IntersectionConnectivity,
  ReservationSystem,
  Routing
}
import pl.edu.agh.wmazur.avs.model.entity.intersection.reservation.GridReservationManager
import pl.edu.agh.wmazur.avs.model.entity.intersection.reservation.ReservationArray.Timestamp
import pl.edu.agh.wmazur.avs.model.entity.intersection.workers.{
  DriversFetcherAgent,
  IntersectionCoordinator
}
import pl.edu.agh.wmazur.avs.model.entity.road.Road
import pl.edu.agh.wmazur.avs.model.entity.utils.SpatialUtils
import pl.edu.agh.wmazur.avs.simulation.EntityManager.SpawnResult
import pl.edu.agh.wmazur.avs.{Agent, EntityRefsGroup}

class AutonomousIntersectionManager(
    val intersection: Intersection,
    val gridManagerConfig: GridReservationManager.ManagerConfig,
    val roads: List[Road],
    val context: ActorContext[IntersectionManager.Protocol],
    val timers: TimerScheduler[IntersectionManager.Protocol],
    val workers: Workers
) extends Agent[IntersectionManager.Protocol]
    with IntersectionManager
    with IntersectionConnectivity
    with ReservationSystem
    with ClosedIntersectionPolicy
    with DefaultPolicy
    with Routing {

  var currentTime: Timestamp = 0L

  def switchBehavior(behavior: Behavior[IntersectionManager.Protocol])
    : Behavior[IntersectionManager.Protocol] = {
    behavior
      .orElse(basicConnectivity)
      .orElse(reservationsManagement)
      .orElse(routing)
      .orElse {
        Behaviors.receiveMessage { msg =>
          context.log.warning("Unhandled message: {}", msg)
          Behaviors.same
        }
      }
  }

  override protected val initialBehaviour
    : Behavior[IntersectionManager.Protocol] =
    switchBehavior(defaultPolicy)
}

object AutonomousIntersectionManager {
  case class Workers(
      driversFetcher: ActorRef[DriversFetcherAgent.Protocol],
      coordinator: ActorRef[IntersectionCoordinator.Protocol]
  )

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
        val driversFetcherManager: ActorRef[DriversFetcherAgent.Protocol] =
          ctx.spawn(
            DriversFetcherAgent.init(
              intersectionPosition = intersection.position,
              intersectionGeometry =
                SpatialUtils.shapeFactory.getGeometryFrom(intersection.area),
              entryPoints = intersection.entryPoints,
              exitPoints = intersection.exitPoints,
              transmitionDistance =
                IntersectionConnectivity.maxTransmitionDistance,
              intersectionManager = ctx.self
            ),
            "drivers-fetcher"
          )

        // format: off
        val coordinator: ActorRef[IntersectionCoordinator.Protocol] =
          ctx.spawn(
            IntersectionCoordinator.init(
              intersectionManagerRef = ctx.self,
              intersection = intersection
            ),
            s"coordinator-${intersection.id}"
          )
        // format: on

        val workers = Workers(driversFetcherManager, coordinator)

        new AutonomousIntersectionManager(intersection = intersection,
                                          gridManagerConfig = managerConfig,
                                          roads = roads,
                                          context = ctx,
                                          timers = timers,
                                          workers = workers)
      }
    }

}
