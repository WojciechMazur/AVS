package pl.edu.agh.wmazur.avs.simulation

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import pl.edu.agh.wmazur.avs.Agent
import pl.edu.agh.wmazur.avs.model.entity.intersection.Intersection
import pl.edu.agh.wmazur.avs.model.entity.road.Road
import pl.edu.agh.wmazur.avs.model.entity.vehicle.Vehicle
import pl.edu.agh.wmazur.avs.protocol.SimulationProtocol
import pl.edu.agh.wmazur.avs.protocol.SimulationProtocol.Ack
import pl.edu.agh.wmazur.avs.simulation.CachedStateManager._
import pl.edu.agh.wmazur.avs.simulation.SimulationManager.Protocol.Ack

import scala.collection.mutable

class CachedStateManager(val context: ActorContext[CachedStateManager.Protocol])
    extends Agent[CachedStateManager.Protocol] {
  val cachedVehicles: mutable.Map[Vehicle#Id, Vehicle] = mutable.Map.empty
  val cachedRoads: mutable.Map[Road#Id, Road] = mutable.Map.empty
  val cachedInteresections: mutable.Map[Intersection#Id, Intersection] =
    mutable.Map.empty

  override protected val initialBehaviour: Behavior[Protocol] =
    Behaviors.receiveMessagePartial {
      case GetAll(replyTo) =>
        replyTo ! StateResult(
          vehicles = cachedVehicles.toMap,
          roads = cachedRoads.toMap,
          intersection = cachedInteresections.toMap
        )
        this
      case Get(replyTo, vehicleSet, roadSet, intersectionSet) =>
        replyTo ! StateResult(
          cachedVehicles.toMap.filterKeys(vehicleSet.contains),
          cachedRoads.toMap.filterKeys(roadSet.contains),
          cachedInteresections.toMap.filterKeys(intersectionSet.contains)
        )
        this
      case Update(optAck, vehicles, roads, intersections) =>
        optAck.collect {
          case (ackTo, ack) => ackTo ! ack
        }
        vehicles.foreach {
          case (key, value) => this.cachedVehicles.update(key, value)
        }
        roads.foreach {
          case (key, value) => this.cachedRoads.update(key, value)
        }
        intersections.foreach {
          case (key, value) => this.cachedInteresections.update(key, value)
        }
        this

      case Remove(replyTo, vehicles, roads, intersections) =>
        replyTo ! Ack
        vehicles.foreach(cachedVehicles.remove)
        roads.foreach(cachedRoads.remove)
        intersections.foreach(cachedInteresections.remove)
        this
    }
}
object CachedStateManager {
  def apply: Behavior[Protocol] = Behaviors.setup[Protocol] { ctx =>
    new CachedStateManager(ctx)
  }

  sealed trait Protocol extends SimulationProtocol

  trait StateRequesterProtocol {
    self: SimulationProtocol =>
  }
  trait StateEditorProtocol {
    self: SimulationProtocol =>
  }

  case class StateResult(
      vehicles: Map[Vehicle#Id, Vehicle] = Map.empty,
      roads: Map[Road#Id, Road] = Map.empty,
      intersection: Map[Intersection#Id, Intersection] = Map.empty
  ) extends Protocol
      with StateRequesterProtocol

  case class Update(
      replyTo: Option[(ActorRef[Ack], Ack)],
      vehicles: Map[Vehicle#Id, Vehicle] = Map.empty,
      roads: Map[Road#Id, Road] = Map.empty,
      intersection: Map[Intersection#Id, Intersection] = Map.empty
  ) extends Protocol
      with StateEditorProtocol

  case class GetAll(replyTo: ActorRef[StateRequesterProtocol]) extends Protocol

  case class Get(replyTo: ActorRef[StateRequesterProtocol],
                 vehicles: Set[Vehicle#Id] = Set.empty,
                 roads: Set[Road#Id] = Set.empty,
                 intersections: Set[Intersection#Id] = Set.empty)
      extends Protocol

  case class Remove(replyTo: ActorRef[SimulationProtocol.Ack],
                    vehicles: Set[Vehicle#Id] = Set.empty,
                    roads: Set[Road#Id] = Set.empty,
                    intersections: Set[Intersection#Id] = Set.empty)
      extends Protocol
}
