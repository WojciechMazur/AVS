package pl.edu.agh.wmazur.avs.simulation

import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior, Terminated}
import org.locationtech.spatial4j.shape.Point
import pl.edu.agh.wmazur.avs.model.entity.Entity
import pl.edu.agh.wmazur.avs.model.entity.intersection.{
  AutonomousIntersectionManager,
  Intersection,
  IntersectionManager
}
import pl.edu.agh.wmazur.avs.model.entity.road.{Lane, Road, RoadManager}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{Angle, Velocity}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.{
  AutonomousVehicleDriver,
  VehicleDriver
}
import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.vehicle.{Vehicle, VehicleSpec}
import pl.edu.agh.wmazur.avs.protocol.SimulationProtocol
import pl.edu.agh.wmazur.avs.simulation.EntityManager.{
  CollectProtocol,
  SpawnProtocol,
  TerminationWatcher
}
import pl.edu.agh.wmazur.avs.model.entity.intersection.reservation.GridReservationManager
import pl.edu.agh.wmazur.avs.model.entity.intersection.workers.GlobalNavigator
import pl.edu.agh.wmazur.avs.simulation.stage.VehiclesCollectorStage
import pl.edu.agh.wmazur.avs.simulation.stage.VehiclesCollectorStage.Done
import pl.edu.agh.wmazur.avs.{Agent, Services}

import scala.collection.mutable
import scala.util.Random
import scala.concurrent.duration._
class EntityManager(val context: ActorContext[EntityManager.Protocol],
                    globalNavigator: ActorRef[GlobalNavigator.Protocol])
    extends Agent[EntityManager.Protocol] {

  val roadsRegistry = mutable.Set.empty[Road#Id]

  def randomDestination: Road#Id =
    roadsRegistry.iterator
      .drop(Random.nextInt(roadsRegistry.size))
      .next()

  override protected val initialBehaviour: Behavior[EntityManager.Protocol] =
    Behaviors.receiveMessagePartial {
      case SpawnProtocol.SpawnBasicVehicle(replyTo,
                                           spec,
                                           position,
                                           heading,
                                           velocity,
                                           lane,
                                           _) =>
        val vehicleId = Vehicle.nextId

        context.spawn(
          AutonomousVehicleDriver.init(
            id = vehicleId,
            spec,
            position,
            heading,
            velocity,
            lane = lane,
            replyTo = replyTo,
            navigatorRef = globalNavigator
          ),
          s"autonomous-driver-$vehicleId",
        )
        Behaviors.same

      case SpawnProtocol.SpawnRoad(replyTo, lanes, opositeRoad) =>
        val roadId = Road.nextId

        context.spawn(
          RoadManager.init(Some(roadId), lanes, opositeRoad, replyTo),
          s"road-manager-$roadId")
        Behaviors.same

      case SpawnProtocol.SpawnOppositeRoads(replyTo, lanes, oppositeLanes) =>
        val roadId = Road.nextId
        roadsRegistry.update(roadId, included = true)
        val road1 =
          context.spawn(RoadManager.init(Some(roadId), lanes, None, replyTo),
                        s"road-manager-$roadId")
        context.self ! SpawnProtocol.SpawnRoad(replyTo,
                                               oppositeLanes,
                                               Some(road1))
        Behaviors.same

      case SpawnProtocol.SpawnAutonomousIntersection(replyTo, roads) =>
        val intersectionId = Intersection.nextId
        val managerConfig = GridReservationManager.ManagerConfig(
          timeStep = 0.1.seconds,
          granularity = 0.4.meters
        )(_internalTimeBuffer = 0.seconds)

        context.spawn(
          AutonomousIntersectionManager.init(Some(intersectionId),
                                             roads,
                                             managerConfig,
                                             replyTo),
          s"autonomous-intersection-$intersectionId"
        )

        Behaviors.same

      case CollectProtocol.CollectDrivers(replyTo, driversRef) =>
        replyTo.foreach { replyTo =>
          context.spawn(
            TerminationWatcher
              .init(replyTo, Done, driversRef),
            s"termination-watcher-${System.nanoTime()}"
          )
        }

        driversRef.foreach(_ ! AutonomousVehicleDriver.Stop)
        Behaviors.same
    }
}

object EntityManager {
  def apply(): Behavior[Protocol] = Behaviors.setup { ctx =>
    ctx.system.receptionist ! Receptionist.register(Services.entityManager,
                                                    ctx.self)
    val navigator = ctx.spawn(GlobalNavigator.init, "global-navigator")
    new EntityManager(ctx, navigator)
  }

  sealed trait Protocol extends SimulationProtocol
  sealed trait SpawnProtocol extends Protocol
  case class Reply[T](ref: ActorRef[T], replyMsg: T, failureMsg: T)
  case class SpawnResult[T <: Entity, Protocol](entity: T,
                                                ref: ActorRef[Protocol])
      extends EntityManager.Protocol
  object SpawnProtocol {
    case class SpawnBasicVehicle[T](
        // format: off
        replyTo: Option[ActorRef[SpawnResult[Vehicle, VehicleDriver.Protocol]]] = None,
       // format: on
        spec: VehicleSpec,
        position: Point,
        heading: Angle,
        velocity: Velocity,
        lane: Lane,
        initialDestination: Option[Road#Id])
        extends SpawnProtocol

    case class SpawnRoad(
        replyTo: Option[ActorRef[SpawnResult[Road, RoadManager.Protocol]]] =
          None,
        lanes: List[Lane],
        oppositeRoad: Option[ActorRef[RoadManager.Protocol]] = None)
        extends SpawnProtocol

    case class SpawnOppositeRoads(
        replyTo: Option[ActorRef[SpawnResult[Road, RoadManager.Protocol]]] =
          None,
        lanes: List[Lane],
        oppositeLanes: List[Lane]
    ) extends SpawnProtocol

    case class SpawnAutonomousIntersection(
        // format: off
        replyTo: Option[ActorRef[SpawnResult[Intersection, IntersectionManager.Protocol]]] = None,
        // format: on
        roads: List[Road])
        extends SpawnProtocol
  }
  sealed trait CollectProtocol extends Protocol
  object CollectProtocol {
    case class EntityTerminated[T](entityRef: ActorRef[T]) extends Protocol
    case class CollectDrivers(
        replyTo: Option[ActorRef[VehiclesCollectorStage.Protocol]] = None,
        driversRef: Set[ActorRef[AutonomousVehicleDriver.ExtendedProtocol]]
    ) extends CollectProtocol

  }

  object TerminationWatcher {
    def init[T, U](replyTo: ActorRef[T],
                   doneMsg: T,
                   waitForRefs: Set[ActorRef[U]]): Behavior[Any] =
      Behaviors.setup { ctx =>
        waitForRefs.foreach(ctx.watch)
        waitForTermination(replyTo, doneMsg, waitForRefs)
      }

    def waitForTermination[T, U](replyTo: ActorRef[T],
                                 doneMsg: T,
                                 awaiting: Set[ActorRef[U]]): Behavior[Any] = {
      if (awaiting.isEmpty) {
        replyTo ! doneMsg
        Behaviors.stopped
      } else {
        Behaviors.receiveSignal {
          case (_, Terminated(ref: ActorRef[U] @unchecked)) =>
            waitForTermination(replyTo, doneMsg, awaiting - ref)
        }
      }
    }
  }
}
