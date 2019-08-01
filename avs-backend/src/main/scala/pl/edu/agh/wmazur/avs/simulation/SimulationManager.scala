package pl.edu.agh.wmazur.avs.simulation

import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import pl.edu.agh.wmazur.avs
import pl.edu.agh.wmazur.avs.model.entity.road.{
  DirectedLane,
  LaneSpec,
  RoadManager
}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.Vehicle
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousDriver
import pl.edu.agh.wmazur.avs.model.state.SimulationState
import pl.edu.agh.wmazur.avs.protocol.SimulationProtocol
import pl.edu.agh.wmazur.avs.protocol.SimulationProtocol.Ack
import pl.edu.agh.wmazur.avs.simulation.SimulationManager.AdapterProtocol.{
  DriversListing,
  RoadsListing
}
import pl.edu.agh.wmazur.avs.simulation.SimulationManager.Protocol
import pl.edu.agh.wmazur.avs.simulation.stage.{
  DriversMovementStage,
  SimulationStateGatherer,
  VehiclesCollectorStage,
  VehiclesSpawnerStage
}
import pl.edu.agh.wmazur.avs.{EntityRefsGroup, Services}

import scala.concurrent.duration._
import scala.concurrent.duration.{Duration, FiniteDuration}

class SimulationManager(context: ActorContext[Protocol],
                        workers: SimulationStageWorkers)
    extends AbstractBehavior[Protocol] {
  import SimulationManager.Protocol._

  private var currentTime = 0L
  private var tick = avs.Tick(0)
  private var lastTickDelta = Duration.Zero

  private var cachedSimulationState: SimulationState = SimulationState.empty
  private var cachedRoadsRefs = Set.empty[ActorRef[RoadManager.Protocol]]
  private var cachedDriversRefs = Set.empty[ActorRef[AutonomousDriver.Protocol]]

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

          context.log.debug(
            s"Tick - {} @ {}, ups: ${1.seconds / tickContext.timeDelta}",
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
          context.log.debug("Spawned {} vehicles", spawnedVehicles.size)
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
        case other =>
          context.log.warning("Unhandled message {} in {}",
                              other,
                              context.self.path)
          Behaviors.same
      }
  }

  object OnBehaviorsSwitch {
    import ActorBehaviors._
    def collectVehicleToDeletion(): Behavior[Protocol] = {
      workers.vehiclesCollector ! VehiclesCollectorStage.TryCollect(
        context.self,
        cachedRoadsRefs,
        currentTime)
      withDefaultBehavior {
        Behaviors.receiveMessagePartial {
          case CollectResult(markedVehicles) =>
            context.log.debug("Collected {} drivers to deletion",
                              markedVehicles.size)
            cachedDriversRefs --= markedVehicles
            if (markedVehicles.nonEmpty) {
              println(markedVehicles)
            }
            gatherCurrentState()
        }
      }
    }

    def gatherCurrentState(): Behavior[Protocol] = {
      workers.stateGatherer ! SimulationStateGatherer.GetCurrentState(
        context.self,
        cachedRoadsRefs,
        cachedDriversRefs,
        currentTime,
        lastTickDelta,
      )
      withDefaultBehavior {
        Behaviors.receiveMessagePartial {
          case update @ StateUpdate(state) =>
            context.log.debug("Gathered new state")
            cachedSimulationState = state
            SimulationEngine.stateUpdateRef ! update
            waitingForTick
        }
      }
    }

    def waitForSpawners(): Behavior[Protocol] = {
      context.log.debug("Waiting for spawners finished")
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
          context.log.debug("Drivers move step finished")
          collectVehicleToDeletion()
      }
    }
  }

  override def onMessage(msg: Protocol): Behavior[Protocol] = {
    context.log.debug("Initial behaviour waitingForTick")
    context.self ! msg
    idle
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
      enityGroupKey <- List(EntityRefsGroup.driver, EntityRefsGroup.road)
    } ctx.system.receptionist ! Receptionist.subscribe(
      enityGroupKey,
      AdapterProtocol.listingAdapter(ctx)
    )

    recoverState(simulationWorkers.get.entityManager)
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

    case class Result(ackTo: ActorRef[Ack], context: StateUpdate)
        extends Protocol
    case class SimulationTickContext(timeDelta: FiniteDuration,
                                     externalChanges: List[StateUpdateCommand])
    case class StateUpdate(state: SimulationState) extends Protocol

    case class SpawnResult(
        spawnedVehicles: Map[ActorRef[AutonomousDriver.Protocol], Vehicle#Id])
        extends Protocol
    case class CollectResult(
        markedVehicles: Set[ActorRef[AutonomousDriver.Protocol]]
    ) extends Protocol

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
      }
    }
    case class DriversListing(drivers: Set[ActorRef[AutonomousDriver.Protocol]])
        extends Protocol
    case class RoadsListing(roads: Set[ActorRef[RoadManager.Protocol]])
        extends Protocol
  }
  private def recoverState(
      entityManagerRef: ActorRef[EntityManager.Protocol]): Unit = {
    import avs.Dimension
    val laneSpec = new LaneSpec(30, 2.5)
    val lane11 =
      DirectedLane.simple(spec = laneSpec,
                          offStartX = -250.0.fromMeters,
                          length = 500.0.fromMeters)

    val lane12 = DirectedLane.simple(
      spec = laneSpec.copy(speedLimit = 10),
      offStartX = -250.0.fromMeters,
      offStartY = laneSpec.width + 0.5.fromMeters,
      length = 500.0.fromMeters)

    val lane13 = DirectedLane.simple(
      spec = laneSpec.copy(speedLimit = 10),
      offStartX = -250.0.fromMeters,
      offStartY = 2 * laneSpec.width + 2 * 0.5.fromMeters,
      length = 500.0.fromMeters,
    )
    val lane21 = DirectedLane.simple(spec = laneSpec,
                                     offStartY = -100.0.fromMeters,
                                     length = 200.0.fromMeters,
                                     heading = Math.PI / 2)

    val lane22 = DirectedLane.simple(spec = laneSpec,
                                     offStartY = -100d,
                                     offStartX = laneSpec.width + 0.5,
                                     length = 250.0,
                                     heading = Math.PI / 2)

    val lane23 = DirectedLane.simple(spec = laneSpec,
                                     offStartY = -100d,
                                     offStartX = 2 * laneSpec.width + 2 * 0.5,
                                     length = 300.0,
                                     heading = Math.PI / 2)

    entityManagerRef ! EntityManager.SpawnProtocol.SpawnRoad(
      lanes = lane11 :: lane12 :: lane13 :: Nil)
    entityManagerRef ! EntityManager.SpawnProtocol.SpawnRoad(
      lanes = lane21 :: lane22 :: lane23 :: Nil)
  }

}
