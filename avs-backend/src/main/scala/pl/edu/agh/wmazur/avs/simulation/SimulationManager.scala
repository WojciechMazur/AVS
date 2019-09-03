package pl.edu.agh.wmazur.avs.simulation

import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import akka.util.Timeout
import pl.edu.agh.wmazur.avs
import pl.edu.agh.wmazur.avs.model.entity.intersection.{
  Intersection,
  IntersectionManager
}
import pl.edu.agh.wmazur.avs.model.entity.road.{Road, RoadManager}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.Vehicle
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousVehicleDriver
import pl.edu.agh.wmazur.avs.model.state.SimulationState
import pl.edu.agh.wmazur.avs.protocol.SimulationProtocol
import pl.edu.agh.wmazur.avs.protocol.SimulationProtocol.Ack
import pl.edu.agh.wmazur.avs.simulation.SimulationManager.AdapterProtocol.{
  DriversListing,
  IntersectionsListing,
  RoadsListing,
  TickSubscribersListing
}
import pl.edu.agh.wmazur.avs.simulation.SimulationManager.Protocol
import pl.edu.agh.wmazur.avs.simulation.SimulationManager.Protocol.RecoveryResult.{
  RecoveryFailed,
  RecoveryFinished
}
import pl.edu.agh.wmazur.avs.simulation.stage.{
  DriversMovementStage,
  SimulationStateGatherer,
  VehiclesCollectorStage,
  VehiclesSpawnerStage
}
import pl.edu.agh.wmazur.avs.{EntityRefsGroup, Services}

import scala.concurrent.duration.{Duration, FiniteDuration, _}
import scala.util.Success

class SimulationManager(context: ActorContext[Protocol],
                        workers: SimulationStageWorkers)
    extends AbstractBehavior[Protocol] {
  import SimulationManager.Protocol._

  private var sysTime = System.currentTimeMillis()

  private var currentTime = 0L
  private var tick = avs.Tick(0)
  private var lastTickDelta = Duration.Zero

  private var cachedSimulationState: SimulationState = SimulationState.empty

  private var cachedRoadsRefs = Set.empty[ActorRef[RoadManager.Protocol]]
  private var cachedIntersectionRefs =
    Set.empty[ActorRef[IntersectionManager.Protocol]]
  private var cachedDriversRefs =
    Set.empty[ActorRef[AutonomousVehicleDriver.ExtendedProtocol]]
  private var cachedTickSubscribersRefs =
    Set.empty[ActorRef[SimulationProtocol.Tick]]

  private val idle = withDefaultBehavior(ActorBehaviors.waitingForTick)

  def withDefaultBehavior(behavior: Behavior[Protocol]): Behavior[Protocol] = {
    behavior.orElse(ActorBehaviors.basicBehavior)
  }

  object ActorBehaviors {
    import OnBehaviorsSwitch._

    lazy val waitingForTick: Behaviors.Receive[Protocol] =
      Behaviors.receiveMessagePartial {
        case Tick(ackTo: ActorRef[Ack], tickContext: SimulationTickContext) =>
          ackTo ! Ack

          currentTime += tickContext.timeDelta.toMillis
          tick = tick.next
          lastTickDelta = tickContext.timeDelta

          val tickMessage = SimulationProtocol.Tick.Default(currentTime,
                                                            lastTickDelta,
                                                            tick.seq)
          cachedTickSubscribersRefs.foreach(_ ! tickMessage)
          val currentSysTime = System.currentTimeMillis()
          val delta = (currentSysTime - sysTime).millis
          sysTime = currentSysTime
          context.log.debug(s"Tick - {} @ {}, ups: ${1.seconds / delta}",
                            tick,
                            currentTime)
          if (tickContext.externalChanges.nonEmpty) {
            applyExternalCommands(tickContext.externalChanges)
          } else {
            waitForSpawners()
          }
      }

    lazy val waitingForSpawnersFinish: Behavior[Protocol] = {
      Behaviors.receiveMessagePartial {
        case SpawnResult(spawnedVehicles) =>
//          context.log.debug("Spawned {} vehicles", spawnedVehicles.size)
          cachedDriversRefs ++= spawnedVehicles.keySet
          withDefaultBehavior {
            letDriversMove
          }
      }
    }

    lazy val basicBehavior: Behavior[Protocol] =
      Behaviors.receiveMessagePartial {
        case Init(replyTo) =>
          context.log.info("Simulation manager init")
          replyTo ! Ack
          SimulationEngine.stateUpdateRef ! StateUpdate.empty
          Behaviors.same
        case Failure(throwable) =>
          context.log.error("Error in simulation manager: {}",
                            throwable.getMessage)
          Behavior.stopped
        case Complete =>
          context.log.info("Simulation completed")
          Behaviors.stopped
        case RoadsListing(roadRefs) =>
          cachedRoadsRefs = roadRefs
          Behaviors.same
        case DriversListing(driverRefs) =>
          cachedDriversRefs = driverRefs
          Behaviors.same
        case IntersectionsListing(intersections) =>
          cachedIntersectionRefs = intersections
          Behaviors.same
        case TickSubscribersListing(refs) =>
          cachedTickSubscribersRefs = refs
          Behaviors.same
        case other =>
          context.log.warning("Unhandled message {} in {}",
                              other,
                              context.self.path)
          Behaviors.same
      }
  }

  object OnBehaviorsSwitch {
    import ActorBehaviors._

    def recoverState: Behaviors.Receive[Protocol] = {
      val recoveryAgent = context.spawn(
        StateRecoveryAgent.init(workers.entityManager),
        "state-recovery-agent")
      implicit val timeout: Timeout = 60.seconds

      context.ask(recoveryAgent)(StateRecoveryAgent.StartRecovery) {
        case Success(RecoveryFinished(roads, intersection)) =>
          RecoveryFinished(roads, intersection)
        case Success(failed: RecoveryFailed) => failed
        case util.Failure(exception)         => RecoveryFailed(exception)
      }

      Behaviors.receiveMessagePartial {
        case RecoveryFinished(roads, intersections) =>
          cachedRoadsRefs ++= roads.values
          cachedIntersectionRefs ++= intersections.values

          context.log.info("Recovery finished")
          idle
        case RecoveryFailed(err) =>
          context.log.error("Recovery failed: {}", err)
          err.printStackTrace()
          Behaviors.stopped
      }
    }

    def collectVehicleToDeletion(): Behavior[Protocol] = {
      workers.vehiclesCollector ! VehiclesCollectorStage.TryCollect(
        context.self,
        cachedRoadsRefs,
        currentTime)
      withDefaultBehavior {
        Behaviors.receiveMessagePartial {
          case CollectResult(markedVehicles) =>
//            context.log.debug("Collected {} drivers to deletion",
//                              markedVehicles.size)
            cachedDriversRefs --= markedVehicles
            gatherCurrentState()
        }
      }
    }

    def gatherCurrentState(): Behavior[Protocol] = {
      workers.stateGatherer ! SimulationStateGatherer.GetCurrentState(
        context.self,
        cachedRoadsRefs,
        cachedDriversRefs,
        cachedIntersectionRefs,
        currentTime,
        lastTickDelta,
      )
      withDefaultBehavior {
        Behaviors.receiveMessagePartial {
          case update @ StateUpdate(state) =>
//            context.log.debug("Gathered new state")
            cachedSimulationState = state
            SimulationEngine.stateUpdateRef ! update
            waitingForTick
        }
      }
    }

    def waitForSpawners(): Behavior[Protocol] = {
//      context.log.debug("Waiting for spawners finished")
      workers.vehiclesSpawner ! VehiclesSpawnerStage.TrySpawn(context.self,
                                                              cachedRoadsRefs,
                                                              currentTime,
                                                              tick)
      withDefaultBehavior(waitingForSpawnersFinish)
    }

    def applyExternalCommands(
        commands: List[StateUpdateCommand]): Behavior[Protocol] = {
      context.log.debug("Applying external commands")
      ???
      withDefaultBehavior(applyingExternalCommands)
    }

    lazy val applyingExternalCommands: Behavior[Protocol] =
      Behaviors
        .receiveMessagePartial[Protocol] {
          case Done =>
            context.log.debug(
              "Applying external changes finished. Waiting for spawners")
            withDefaultBehavior(waitingForSpawnersFinish)
          //        applingCommands
        }

    def letDriversMove: Behavior[Protocol] = {
      workers.driversMovement ! DriversMovementStage.Step(
        replyTo = context.self,
        roadRefs = cachedRoadsRefs,
        driversRefs = cachedDriversRefs,
        currentTime = currentTime,
        tickDelta = lastTickDelta)

      Behaviors.receiveMessagePartial {
        case Done =>
//          context.log.debug("Drivers move step finished")
          collectVehicleToDeletion()
      }
    }
  }

  override def onMessage(msg: Protocol): Behavior[Protocol] = {
    context.log.debug("Initial behaviour waitingForTick")
    context.self ! msg
    withDefaultBehavior(OnBehaviorsSwitch.recoverState)
  }
}

object SimulationManager {
  def apply(): Behavior[Protocol] = Behaviors.setup[Protocol] { ctx =>
    ctx.system.receptionist ! Receptionist
      .register(Services.simulationManager, ctx.self)

    val simulationWorkers = for {
      entityManager <- Some(ctx.spawn(EntityManager(), "entity-manager"))
      stateGatherer = ctx.spawn(SimulationStateGatherer.init,
                                "simulation-state-gatherer-stage")
      vehicleSpawner = ctx.spawn(VehiclesSpawnerStage.init,
                                 "vehicles-spawner-stage")
      vehicleCollector = ctx.spawn(VehiclesCollectorStage.init,
                                   "vehicles-collector-stage")
      driversMovment = ctx.spawn(DriversMovementStage.init,
                                 "drivers-movement-stage")
    } yield
      SimulationStageWorkers(
        entityManager = entityManager,
        vehiclesSpawner = vehicleSpawner,
        vehiclesCollector = vehicleCollector,
        stateGatherer = stateGatherer,
        driversMovement = driversMovment
      )

    for {
      enityGroupKey <- List(EntityRefsGroup.driver,
                            EntityRefsGroup.road,
                            EntityRefsGroup.intersection,
                            EntityRefsGroup.tickSubscribers)
    } ctx.system.receptionist ! Receptionist.subscribe(
      enityGroupKey,
      AdapterProtocol.listingAdapter(ctx)
    )
    new SimulationManager(ctx, simulationWorkers.get)
  }

  sealed trait Protocol extends SimulationProtocol
  object Protocol {
    case object Ack extends SimulationProtocol.Ack with Protocol
    case object Done extends SimulationProtocol.Done with Protocol
    case object Complete extends Protocol
    case class Failure(throwable: Throwable) extends Protocol {
      println(s"Simulation Failure: $throwable")
      throwable.printStackTrace()
    }
    case class Init(ackTo: ActorRef[Ack]) extends Protocol

    case class Tick(ackTo: ActorRef[Ack], context: SimulationTickContext)
        extends Protocol
        with SimulationProtocol.Tick

    case class Result(ackTo: ActorRef[Ack], context: StateUpdate)
        extends Protocol
    case class SimulationTickContext(timeDelta: FiniteDuration,
                                     externalChanges: List[StateUpdateCommand])
    case class StateUpdate(state: SimulationState) extends Protocol

    case class SpawnResult(
        spawnedVehicles: Map[ActorRef[AutonomousVehicleDriver.ExtendedProtocol],
                             Vehicle#Id])
        extends Protocol
    case class CollectResult(
        markedVehicles: Set[ActorRef[AutonomousVehicleDriver.ExtendedProtocol]]
    ) extends Protocol

    sealed trait RecoveryResult extends Protocol
    object RecoveryResult {
      case class RecoveryFinished(
          roads: Map[Road, ActorRef[RoadManager.Protocol]],
          intersections: Map[Intersection,
                             ActorRef[IntersectionManager.Protocol]]
      ) extends RecoveryResult
      case class RecoveryFailed(throwable: Throwable) extends RecoveryResult
    }

    object StateUpdate {
      def empty: StateUpdate = StateUpdate(SimulationState.empty)
    }
  }
  object AdapterProtocol {
    def listingAdapter(context: ActorContext[SimulationManager.Protocol])
      : ActorRef[Receptionist.Listing] = {
      context.messageAdapter[Receptionist.Listing] {
        case EntityRefsGroup.road.Listing(refs)   => RoadsListing(refs)
        case EntityRefsGroup.driver.Listing(refs) => DriversListing(refs)
        case EntityRefsGroup.intersection.Listing(refs) =>
          IntersectionsListing(refs)
        case EntityRefsGroup.tickSubscribers.Listing(refs) =>
          TickSubscribersListing(refs)
      }
    }

    case class DriversListing(
        drivers: Set[ActorRef[AutonomousVehicleDriver.ExtendedProtocol]])
        extends Protocol
    case class RoadsListing(roads: Set[ActorRef[RoadManager.Protocol]])
        extends Protocol
    case class IntersectionsListing(
        intersections: Set[ActorRef[IntersectionManager.Protocol]])
        extends Protocol
    case class TickSubscribersListing(
        refs: Set[ActorRef[SimulationProtocol.Tick]])
        extends Protocol
  }

}
