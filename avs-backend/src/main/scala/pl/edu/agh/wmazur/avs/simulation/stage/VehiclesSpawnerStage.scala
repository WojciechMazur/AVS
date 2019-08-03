package pl.edu.agh.wmazur.avs.simulation.stage

import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import pl.edu.agh.wmazur.avs.{Services, Tick}
import pl.edu.agh.wmazur.avs.model.entity.road.RoadManager
import pl.edu.agh.wmazur.avs.model.entity.vehicle.Vehicle
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousVehicleDriver
import pl.edu.agh.wmazur.avs.simulation.SimulationManager.Protocol.SpawnResult
import pl.edu.agh.wmazur.avs.simulation.reservation.ReservationArray.Timestamp
import pl.edu.agh.wmazur.avs.simulation.{EntityManager, SimulationManager}

object VehiclesSpawnerStage {
  sealed trait Protocol
  case class TrySpawn(replyTo: ActorRef[SimulationManager.Protocol],
                      roadRefs: Set[ActorRef[RoadManager.Protocol]],
                      currentTime: Timestamp,
                      tick: Tick)
      extends Protocol
  case class RoadSpawnResult(
      roadRef: ActorRef[RoadManager.Protocol],
      spawned: Map[ActorRef[AutonomousVehicleDriver.ExtendedProtocol],
                   Vehicle#Id])
      extends Protocol
  case class NotSpawned(roadRef: ActorRef[RoadManager.Protocol])
      extends Protocol
  case class EntityManagerListing(refs: Set[ActorRef[EntityManager.Protocol]])
      extends Protocol

  val init: Behavior[Protocol] =
    Behaviors.setup[Protocol] { ctx =>
      val adapter = ctx.messageAdapter[Receptionist.Listing] {
        case Services.entityManager.Listing(refs) => EntityManagerListing(refs)
      }
      ctx.system.receptionist ! Receptionist.subscribe(Services.entityManager,
                                                       adapter)
      Behaviors.receiveMessagePartial {
        case EntityManagerListing(refs) if refs.nonEmpty =>
          ctx.log.info("Vehicle spawner stage ready")
          idle(refs.head)
      }
    }

  private def idle(
      entityManagerRef: ActorRef[EntityManager.Protocol]): Behavior[Protocol] =
    Behaviors.receive {
      case (ctx, TrySpawn(replyTo, roadRefs, currentTime, tick)) =>
        ctx.children
          .filter(_.path.name.contains("spawner"))
          .foreach(ref => ctx.stop(ref))

        if (roadRefs.isEmpty) {
          replyTo ! SpawnResult(Map.empty)
          idle(entityManagerRef)
        } else {

          roadRefs.foreach { ref =>
//            ctx.watchWith(ref, NotSpawned(ref))
            ref ! RoadManager.TrySpawn(ctx.self, entityManagerRef, currentTime)
          }
          awaitResponses(entityManagerRef, replyTo, roadRefs, Map.empty)
        }
      case (_, EntityManagerListing(refs)) =>
        idle(refs.head)
    }

  private def awaitResponses(
      entityManagerRef: ActorRef[EntityManager.Protocol],
      replyTo: ActorRef[SimulationManager.Protocol.SpawnResult],
      awaiting: Set[ActorRef[RoadManager.Protocol]],
      results: Map[ActorRef[AutonomousVehicleDriver.ExtendedProtocol],
                   Vehicle#Id]): Behaviors.Receive[Protocol] = {

    def checkIfReady(
        awaiting: Set[ActorRef[RoadManager.Protocol]],
        results: Map[ActorRef[AutonomousVehicleDriver.ExtendedProtocol],
                     Vehicle#Id]): Behavior[Protocol] = {
      if (awaiting.isEmpty) {
        replyTo ! SpawnResult(results)
        idle(entityManagerRef)
      } else {
        awaitResponses(entityManagerRef, replyTo, awaiting, results)
      }
    }

    Behaviors.receiveMessage[Protocol] {
      case EntityManagerListing(refs) =>
        awaitResponses(refs.head, replyTo, awaiting, results)
      case RoadSpawnResult(roadRef, roadResults) =>
        checkIfReady(awaiting - roadRef, results ++ roadResults)
      case NotSpawned(ref) =>
        checkIfReady(awaiting - ref, results)
    }
  }

}
