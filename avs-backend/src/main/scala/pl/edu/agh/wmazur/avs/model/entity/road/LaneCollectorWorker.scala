package pl.edu.agh.wmazur.avs.model.entity.road

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import org.locationtech.spatial4j.shape.{Point, Shape}
import pl.edu.agh.wmazur.avs.Agent
import pl.edu.agh.wmazur.avs.model.entity.road.LaneCollectorWorker.Protocol.{
  PositionReading,
  TryCollect
}
import pl.edu.agh.wmazur.avs.model.entity.road.LaneCollectorWorker._
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousDriver
import pl.edu.agh.wmazur.avs.protocol.SimulationProtocol

class LaneCollectorWorker(
    val context: ActorContext[Protocol],
    roadCollectorWorker: ActorRef[RoadCollectorWorker.Protocol],
    lane: Lane)
    extends Agent[Protocol] {
  val collectorPoint: Option[CollectorPoint] = lane.collectorPoint
  val adapter: ActorRef[AutonomousDriver.PositionReading] =
    context.messageAdapter[AutonomousDriver.PositionReading] {
      case AutonomousDriver.PositionReading(driverRef, position, area) =>
        PositionReading(driverRef, position, area)
    }

  override protected val initialBehaviour: Behavior[Protocol] = idle

  lazy val idle: Behavior[Protocol] = Behaviors.receiveMessage {
    case TryCollect(drivers) =>
      if (collectorPoint.isEmpty) {
        roadCollectorWorker ! RoadCollectorWorker.LaneCollectResult(
          context.self,
          Set.empty)
        idle
      } else {
        drivers.foreach(_ ! AutonomousDriver.GetPositionReading(adapter))
        getReadings(drivers, Set.empty)
      }
  }

  def getReadings(awaiting: Set[ActorRef[AutonomousDriver.Protocol]],
                  markedToDeletion: Set[ActorRef[AutonomousDriver.Protocol]])
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
        vehiclesAtLane: Set[ActorRef[AutonomousDriver.Protocol]])
        extends Protocol

    case class PositionReading(driverRef: ActorRef[AutonomousDriver.Protocol],
                               position: Point,
                               area: Shape)
        extends Protocol

  }

}
