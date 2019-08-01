package pl.edu.agh.wmazur.avs.model.state

import pl.edu.agh.wmazur.avs.model.entity.Entity
import pl.edu.agh.wmazur.avs.model.entity.intersection.Intersection
import pl.edu.agh.wmazur.avs.model.entity.road.Road
import pl.edu.agh.wmazur.avs.model.entity.utils.DeltaOps
import pl.edu.agh.wmazur.avs.model.entity.vehicle.Vehicle
import pl.edu.agh.wmazur.avs.model.state.SimulationState.{
  IntersectionId,
  RoadId,
  VehicleId
}
import pl.edu.agh.wmazur.avs.model.state.SimulationStateUpdate.EntitiesUpdate

import scala.concurrent.duration.{Duration, FiniteDuration}

sealed trait SimulationStateUpdate {
  def isDelta: Boolean
  def timeDelta: FiniteDuration
  def timestamp: Long
  def vehicles: EntitiesUpdate[VehicleId, Vehicle]
  def roads: EntitiesUpdate[RoadId, Road]
  def intersections: EntitiesUpdate[IntersectionId, Intersection]
}

object SimulationStateUpdate {
  case class EntitiesUpdate[K <: Entity#Id, V <: Entity](
      created: Iterable[V] = Iterable.empty,
      updated: Iterable[V] = Iterable.empty,
      removed: Iterable[K] = Iterable.empty) {

    def plus(that: EntitiesUpdate[K, V]): EntitiesUpdate[K, V] = {
      def distinct(that: Iterable[V], other: Iterable[V]) =
        (that ++ other).groupBy(_.id).mapValues(_.last).values
      def distinctKeys(that: Iterable[K], other: Iterable[K]) =
        (that ++ other).toSet

      EntitiesUpdate(
        created = distinct(this.created, that.created),
        updated = distinct(this.updated, that.updated),
        removed = distinctKeys(this.removed, that.removed)
      )
    }
  }

  def entityDelta[K <: Entity#Id, V <: DeltaOps[V] with Entity](
      previousState: Map[K, V],
      currentState: Map[K, V]): EntitiesUpdate[K, V] = {
    val created = currentState -- previousState.keySet
    val deleted = previousState.keySet diff currentState.keySet
    val updated = (currentState -- created.keySet -- deleted).filter {
      case (key, value) =>
        val previous = previousState(key)
        value isUpdatedBy previous
    }

    EntitiesUpdate(
      created = created.values,
      updated = updated.values,
      removed = deleted
    )
  }

  def apply(previous: SimulationState,
            current: SimulationState): SimulationStateDelta = {
    SimulationStateDelta(
      timeDelta = current.tickDelta,
      timestamp = current.currentTime,
      vehicles = entityDelta(previous.vehicles, current.vehicles),
      roads = entityDelta(previous.roads, current.roads),
      intersections = entityDelta(previous.intersections, current.intersections)
    )

  }

  def apply(current: SimulationState): SimulationStateFullUpdate =
    SimulationStateFullUpdate(
      timestamp = current.currentTime,
      vehicles =
        EntitiesUpdate[VehicleId, Vehicle](created = current.vehicles.values),
      roads = EntitiesUpdate[RoadId, Road](created = current.roads.values),
      intersections = EntitiesUpdate[IntersectionId, Intersection](
        created = current.intersections.values)
    )
}

case class SimulationStateDelta(
    timeDelta: FiniteDuration,
    timestamp: Long,
    vehicles: EntitiesUpdate[VehicleId, Vehicle],
    roads: EntitiesUpdate[RoadId, Road],
    intersections: EntitiesUpdate[RoadId, Intersection]
) extends SimulationStateUpdate {
  override def isDelta: Boolean = true
}

object SimulationStateDelta {
  val empty: SimulationStateDelta =
    SimulationStateDelta(Duration.Zero,
                         0L,
                         EntitiesUpdate[VehicleId, Vehicle](),
                         EntitiesUpdate[RoadId, Road](),
                         EntitiesUpdate[RoadId, Intersection]())

}

case class SimulationStateFullUpdate(
    timestamp: Long,
    vehicles: EntitiesUpdate[VehicleId, Vehicle],
    roads: EntitiesUpdate[RoadId, Road],
    intersections: EntitiesUpdate[RoadId, Intersection]
) extends SimulationStateUpdate {
  override def isDelta: Boolean = false
  override def timeDelta: FiniteDuration = Duration.Zero
}
