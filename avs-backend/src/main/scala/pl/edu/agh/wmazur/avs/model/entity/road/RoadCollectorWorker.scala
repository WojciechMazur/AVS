package pl.edu.agh.wmazur.avs.model.entity.road

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import org.locationtech.spatial4j.shape.{Point, Shape}
import pl.edu.agh.wmazur.avs.Agent
import pl.edu.agh.wmazur.avs.model.entity.road.RoadCollectorWorker.{
  PositionReading,
  Protocol,
  TryCollect
}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousDriver
import pl.edu.agh.wmazur.avs.protocol.SimulationProtocol
import pl.edu.agh.wmazur.avs.simulation.EntityManager
import pl.edu.agh.wmazur.avs.simulation.stage.VehiclesCollectorStage

class RoadCollectorWorker(val context: ActorContext[Protocol],
                          roadManagerRef: ActorRef[RoadManager.Protocol])
    extends Agent[Protocol] {
  override protected val initialBehaviour: Behavior[Protocol] = idle
  def adapterForLane(lane: Lane): ActorRef[AutonomousDriver.PositionReading] =
    context.messageAdapter[AutonomousDriver.PositionReading] {
      case AutonomousDriver.PositionReading(driverRef, position, area) =>
        PositionReading(driverRef, lane, position, area)
    }

  lazy val idle: Behaviors.Receive[Protocol] = Behaviors.receiveMessagePartial {
    case TryCollect(replyTo, entityManagerRef, vehiclesAtLanes) =>
      val drivers = for {
        (lane, drivers) <- vehiclesAtLanes.filterKeys(
          _.collectorPoint.isDefined)
        adapter = adapterForLane(lane)
        ref <- drivers
      } yield {
        ref ! AutonomousDriver.GetPositionReading(adapter)
        ref
      }

      getReadings(replyTo, entityManagerRef, drivers.toSet, Set.empty)
  }

  def getReadings(replyTo: ActorRef[VehiclesCollectorStage.Protocol],
                  entityManagerRef: ActorRef[EntityManager.Protocol],
                  awaiting: Set[ActorRef[AutonomousDriver.Protocol]],
                  markedToDeletion: Set[ActorRef[AutonomousDriver.Protocol]])
    : Behavior[Protocol] = {
    if (awaiting.isEmpty) {
      replyTo ! VehiclesCollectorStage.MarkedToDeletion(roadManagerRef,
                                                        markedToDeletion)
      idle
    } else {
      Behaviors.receiveMessagePartial {
        case PositionReading(ref, lane, position, area) =>
          val collectorPoint = lane.collectorPoint.get
          val markedVehicles =
            if (collectorPoint.shouldBeRemoved(position, area)) {
              markedToDeletion + ref
            } else {
              markedToDeletion
            }
          getReadings(replyTo, entityManagerRef, awaiting - ref, markedVehicles)
      }
    }
  }

}
object RoadCollectorWorker {
  def init(roadManagerRef: ActorRef[RoadManager.Protocol]): Behavior[Protocol] =
    Behaviors.setup { ctx =>
      new RoadCollectorWorker(ctx, roadManagerRef)
    }
  sealed trait Protocol extends SimulationProtocol
  case class PositionReading(driverRef: ActorRef[AutonomousDriver.Protocol],
                             lane: Lane,
                             position: Point,
                             area: Shape)
      extends Protocol
  case class TryCollect(
      replyTo: ActorRef[VehiclesCollectorStage.Protocol],
      entityManagerRef: ActorRef[EntityManager.Protocol],
      vehiclesAtLanes: Map[Lane, Set[ActorRef[AutonomousDriver.Protocol]]])
      extends Protocol
}
