package pl.edu.agh.wmazur.avs.model.entity.intersection

import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.{ActorContext, Behaviors, TimerScheduler}
import akka.actor.typed.{ActorRef, Behavior}
import pl.edu.agh.wmazur.avs.model.entity.intersection.AutonomousIntersectionManager.Protocol.FetchDrivers
import pl.edu.agh.wmazur.avs.model.entity.intersection.AutonomousIntersectionManager.Workers
import pl.edu.agh.wmazur.avs.model.entity.intersection.policy.{
  ClosedIntersectionPolicy,
  DefaultPolicy,
  IntersectionConnectivity,
  Routing
}
import pl.edu.agh.wmazur.avs.model.entity.intersection.workers.{
  DriversFetcherAgent,
  IntersectionCoordinator
}
import pl.edu.agh.wmazur.avs.model.entity.road.Road
import pl.edu.agh.wmazur.avs.model.entity.utils.SpatialUtils
import pl.edu.agh.wmazur.avs.simulation.EntityManager.SpawnResult
import pl.edu.agh.wmazur.avs.simulation.reservation.GridReservationManager
import pl.edu.agh.wmazur.avs.{Agent, Dimension, EntityRefsGroup}

import scala.concurrent.duration._

class AutonomousIntersectionManager(
    val intersection: Intersection,
    gridManagerConfig: GridReservationManager.ManagerConfig,
    val roads: List[Road],
    val context: ActorContext[IntersectionManager.Protocol],
    val timers: TimerScheduler[IntersectionManager.Protocol],
    val workers: Workers
) extends Agent[IntersectionManager.Protocol]
    with IntersectionManager
    with ClosedIntersectionPolicy
    with DefaultPolicy
    with IntersectionConnectivity
    with Routing {
  // TODO ACZs

  val reservationManager = GridReservationManager(
    gridManagerConfig,
    intersection
  )

  def switchBehavior(behavior: Behavior[IntersectionManager.Protocol])
    : Behavior[IntersectionManager.Protocol] = {
    behavior.orElse(basicConnectivity).orElse(routing)
  }

  override protected val initialBehaviour
    : Behavior[IntersectionManager.Protocol] =
    switchBehavior(closedIntersection)
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
        timers.startPeriodicTimer(FetchDrivers,
                                  FetchDrivers,
                                  transmitionInterval)

        val driversFetcherManager: ActorRef[DriversFetcherAgent.Protocol] =
          ctx.spawn(
            DriversFetcherAgent.init(
              intersectionPosition = intersection.position,
              intersectionGeometry =
                SpatialUtils.shapeFactory.getGeometryFrom(intersection.area),
              entryPoints = intersection.entryPoints,
              exitPoints = intersection.exitPoints,
              transmitionDistance =
                AutonomousIntersectionManager.maxTransmitionDistance,
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
  val maximumFutureReservationTime: FiniteDuration = 10.seconds
  val defaultACZSize: Dimension = 40.asMeters

  val ACZDistanceShapeLength: Dimension = 1.asMeters
  val transmitionInterval: FiniteDuration = 1.seconds

  val maxTransmitionDistance: Dimension = 200.asMeters
}
