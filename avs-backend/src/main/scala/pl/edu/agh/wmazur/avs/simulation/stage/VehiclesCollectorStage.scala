package pl.edu.agh.wmazur.avs.simulation.stage

import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import pl.edu.agh.wmazur.avs.model.entity.road.RoadManager
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousVehicleDriver
import pl.edu.agh.wmazur.avs.protocol.SimulationProtocol
import pl.edu.agh.wmazur.avs.simulation.reservation.ReservationArray.Timestamp
import pl.edu.agh.wmazur.avs.simulation.stage.VehiclesCollectorStage._
import pl.edu.agh.wmazur.avs.simulation.{EntityManager, SimulationManager}
import pl.edu.agh.wmazur.avs.{Agent, Services}

class VehiclesCollectorStage(val context: ActorContext[Protocol])
    extends Agent[Protocol] {
  context.log.info("Vehicles collector stage spawned")

  var entityManager: ActorRef[EntityManager.Protocol] = _
  override protected val initialBehaviour: Behavior[Protocol] = handleListing

  def handleListing: Behaviors.Receive[Protocol] =
    Behaviors.receiveMessagePartial[Protocol] {
      case EntityManagerListing(refs) if refs.nonEmpty =>
        entityManager = refs.head
        idle
    }

  def idle: Behavior[Protocol] =
    Behaviors
      .receiveMessagePartial[Protocol] {
        case TryCollect(replyTo, roadRefs, currentTime) =>
          val msg =
            RoadManager.TryCollect(context.self, entityManager, currentTime)
          roadRefs.foreach(_ ! msg)
          waitForResults(replyTo, roadRefs, Set.empty)

      }
      .orElse(handleListing)

  def waitForResults(
      replyTo: ActorRef[SimulationManager.Protocol],
      awaiting: Set[ActorRef[RoadManager.Protocol]],
      markedToDeletion: Set[ActorRef[AutonomousVehicleDriver.ExtendedProtocol]]
  ): Behavior[Protocol] = {
    {
      def finalize: Behavior[Protocol] = {
        replyTo ! SimulationManager.Protocol.CollectResult(markedToDeletion)
        idle
      }

      if (awaiting.isEmpty) {
        if (markedToDeletion.nonEmpty) {
          entityManager ! EntityManager.CollectProtocol.CollectDrivers(
            replyTo = Some(context.self),
            driversRef = markedToDeletion)

          Behaviors.receiveMessagePartial[Protocol] {
            case Done =>
              finalize
          }
        } else { finalize }
      } else {
        Behaviors.receiveMessagePartial[Protocol] {
          case MarkedToDeletion(roadManager, refs) =>
            waitForResults(replyTo,
                           awaiting - roadManager,
                           markedToDeletion ++ refs)
        }
      }
    }.orElse(handleListing)
  }
}
object VehiclesCollectorStage {

  sealed trait Protocol extends SimulationProtocol

  case class TryCollect(replyTo: ActorRef[SimulationManager.Protocol],
                        roadRefs: Set[ActorRef[RoadManager.Protocol]],
                        currentTime: Timestamp)
      extends Protocol

  case class MarkedToDeletion(
      roadManager: ActorRef[RoadManager.Protocol],
      refs: Set[ActorRef[AutonomousVehicleDriver.ExtendedProtocol]])
      extends Protocol

  case class EntityManagerListing(refs: Set[ActorRef[EntityManager.Protocol]])
      extends Protocol

  case object Done extends Protocol

  val init: Behavior[Protocol] =
    Behaviors.setup[Protocol] { ctx =>
      val adapter = ctx.messageAdapter[Receptionist.Listing] {
        case Services.entityManager.Listing(refs) => EntityManagerListing(refs)
      }
      ctx.system.receptionist ! Receptionist.subscribe(Services.entityManager,
                                                       adapter)
      new VehiclesCollectorStage(ctx)
    }
}
