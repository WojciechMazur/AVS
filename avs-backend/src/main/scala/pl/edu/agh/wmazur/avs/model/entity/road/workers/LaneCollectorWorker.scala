package pl.edu.agh.wmazur.avs.model.entity.road.workers

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import org.locationtech.spatial4j.shape.{Point, Shape}
import pl.edu.agh.wmazur.avs.Agent
import pl.edu.agh.wmazur.avs.model.entity.road.workers.LaneCollectorWorker.Protocol
import pl.edu.agh.wmazur.avs.model.entity.road.workers.LaneCollectorWorker.Protocol.{
  PositionReading,
  TryCollect
}
import pl.edu.agh.wmazur.avs.model.entity.road.{CollectorPoint, Lane}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousVehicleDriver
import pl.edu.agh.wmazur.avs.protocol.SimulationProtocol

class LaneCollectorWorker(
    val context: ActorContext[Protocol],
    roadCollectorWorker: ActorRef[RoadCollectorWorker.Protocol],
    lane: Lane)
    extends Agent[Protocol] {
  val collectorPoint: Option[CollectorPoint] = lane.collectorPoint
  val adapter: ActorRef[AutonomousVehicleDriver.PositionReading] =
    context.messageAdapter[AutonomousVehicleDriver.PositionReading] {
      case AutonomousVehicleDriver.PositionReading(driverRef,
                                                   position,
                                                   _,
                                                   area) =>
        PositionReading(driverRef, position, area)
    }

  override protected val initialBehaviour: Behavior[Protocol] = idle

  lazy val idle: Behavior[Protocol] = Behaviors.receiveMessagePartial {
    case TryCollect(drivers) =>
      if (collectorPoint.isEmpty) {
        roadCollectorWorker ! RoadCollectorWorker.LaneCollectResult(
          context.self,
          Set.empty)
        idle
      } else {
        drivers.foreach(_ ! AutonomousVehicleDriver.GetPositionReading(adapter))
        getReadings(drivers, Set.empty)
      }
  }

  def getReadings(
      awaiting: Set[ActorRef[AutonomousVehicleDriver.Protocol]],
      markedToDeletion: Set[ActorRef[AutonomousVehicleDriver.Protocol]])
    : Behavior[Protocol] = {
    if (awaiting.isEmpty) {
      roadCollectorWorker ! RoadCollectorWorker.LaneCollectResult(
        context.self,
        markedToDeletion)
      idle
    } else {
      Behaviors.receiveMessagePartial {
        case PositionReading(ref, position, area) =>
          val markedVehicles =
            if (collectorPoint
                  .exists(_.shouldBeRemoved(position, area))) {
              markedToDeletion + ref
            } else {
              markedToDeletion
            }
          getReadings(awaiting - ref, markedVehicles)
      }
    }
  }
}

object LaneCollectorWorker {

  def init(lane: Lane,
           roadCollectorWorker: ActorRef[RoadCollectorWorker.Protocol])
    : Behavior[Protocol] =
    Behaviors.setup { ctx =>
      new LaneCollectorWorker(ctx, roadCollectorWorker, lane)
    }

  sealed trait Protocol extends SimulationProtocol

  object Protocol {
    case class TryCollect(
        vehiclesAtLane: Set[ActorRef[AutonomousVehicleDriver.Protocol]])
        extends Protocol

    case class PositionReading(
        driverRef: ActorRef[AutonomousVehicleDriver.Protocol],
        position: Point,
        area: Shape)
        extends Protocol

  }

}
