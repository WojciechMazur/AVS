package pl.edu.agh.wmazur.avs.simulation

import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior, Terminated}
import org.locationtech.spatial4j.shape.Point
import pl.edu.agh.wmazur.avs.model.entity.road.{
  Lane,
  LaneSpawnerWorker,
  Road,
  RoadManager
}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{Angle, Velocity}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousDriver
import pl.edu.agh.wmazur.avs.model.entity.vehicle.{Vehicle, VehicleSpec}
import pl.edu.agh.wmazur.avs.protocol.SimulationProtocol
import pl.edu.agh.wmazur.avs.protocol.SimulationProtocol.Ack
import pl.edu.agh.wmazur.avs.simulation.EntityManager.{
  CollectProtocol,
  SpawnProtocol,
  TerminationWatcher
}
import pl.edu.agh.wmazur.avs.simulation.stage.VehiclesCollectorStage
import pl.edu.agh.wmazur.avs.simulation.stage.VehiclesCollectorStage.Done
import pl.edu.agh.wmazur.avs.{Agent, Services}

class EntityManager(val context: ActorContext[EntityManager.Protocol])
    extends Agent[EntityManager.Protocol] {

  override protected val initialBehaviour: Behavior[EntityManager.Protocol] =
    Behaviors.receiveMessage {
      case SpawnProtocol.SpawnBasicVehicle(replyTo,
                                           spec,
                                           position,
                                           heading,
                                           velocity,
                                           lane) =>
        val vehicleId = Vehicle.nextId
        val driver =
          AutonomousDriver(id = vehicleId,
                           spec,
                           position,
                           heading,
                           velocity,
                           lane = lane)
        val driverRef = context.spawn(driver, s"autonomous-driver-$vehicleId")
        replyTo ! LaneSpawnerWorker.Spawned(driverRef, vehicleId)
        Behaviors.same

      case SpawnProtocol.SpawnRoad(replyTo, ackMessage, lanes, opositeRoad) =>
        val roadId = Road.nextId
        val roadManager = RoadManager(Some(roadId), lanes, opositeRoad)
        val roadRef = context.spawn(roadManager, s"road-manager-$roadId")

        for {
          ref <- replyTo
          ack <- ackMessage
        } ref ! ack

        Behaviors.same

      case CollectProtocol.CollectDrivers(replyTo, driversRef) =>
        replyTo.foreach { replyTo =>
          context.spawn(
            TerminationWatcher
              .init(replyTo, Done, driversRef),
            s"termination-watcher-${System.nanoTime()}"
          )
        }

        driversRef.foreach(context.stop)
        Behaviors.same
    }
}

object EntityManager {
  def apply(): Behavior[Protocol] = Behaviors.setup { ctx =>
    ctx.system.receptionist ! Receptionist.register(Services.entityManager,
                                                    ctx.self)
    new EntityManager(ctx)
  }

  sealed trait Protocol extends SimulationProtocol
  sealed trait SpawnProtocol extends Protocol
  case class Reply[T](ref: ActorRef[T], replyMsg: T, failureMsg: T)
  object SpawnProtocol {
    case class SpawnBasicVehicle[T](
        replyTo: ActorRef[LaneSpawnerWorker.Protocol],
        spec: VehicleSpec,
        position: Point,
        heading: Angle,
        velocity: Velocity,
        lane: Lane)
        extends SpawnProtocol

    case class SpawnRoad(replyTo: Option[ActorRef[Ack]] = None,
                         ackMessage: Option[Ack] = None,
                         lanes: List[Lane],
                         oppositeRoad: Option[ActorRef[RoadManager.Protocol]] =
                           None)
        extends SpawnProtocol
  }
  sealed trait CollectProtocol extends Protocol
  object CollectProtocol {
    case class EntityTerminated[T](entityRef: ActorRef[T]) extends Protocol
    case class CollectDrivers(
        replyTo: Option[ActorRef[VehiclesCollectorStage.Protocol]] = None,
        driversRef: Set[ActorRef[AutonomousDriver.Protocol]]
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
          case (_, Terminated(ref: ActorRef[U])) =>
            waitForTermination(replyTo, doneMsg, awaiting - ref)
        }
      }
    }
  }
}
