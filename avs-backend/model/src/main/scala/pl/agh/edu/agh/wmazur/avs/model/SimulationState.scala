package pl.agh.edu.agh.wmazur.avs.model
import pl.agh.edu.agh.wmazur.avs.model.SimulationState._
import pl.agh.edu.agh.wmazur.avs.model.entity.intersection.Intersection
import pl.agh.edu.agh.wmazur.avs.model.entity.road.Road
import pl.agh.edu.agh.wmazur.avs.model.entity.utils.DeltaOps
import pl.agh.edu.agh.wmazur.avs.model.entity.vehicle.{DefaultVehicle, Vehicle}
import protobuf.pl.agh.edu.agh.wmazur.avs.model.common.Position

import scala.collection.immutable.{HashMap, Map}
import scala.concurrent.duration._

case class SimulationState(
    totalTicks: Long,
    tickDelta: FiniteDuration,
    vehicles: Map[VehicleId, Vehicle],
    roads: Map[RoadId, Road],
    intersections: Map[IntersectionId, Intersection]
) extends DeltaOps[SimulationState] {

  override def delta(
      previousState: SimulationState): Option[SimulationState] = {
    def newKeys[K, V](old: Map[K, V], `new`: Map[K, V]): Map[K, V] =
      `new` -- old.keySet
    def deltaKeys[K, V <: DeltaOps[V]](
        old: Map[K, V],
        `new`: Map[K, V],
        newlyCreatedKey: Iterable[K]): Map[K, V] = {
      (`new` -- newlyCreatedKey)
        .map { case (key, value) => key -> value.delta(old(key)) }
        .collect { case (key, Some(delta)) => key -> delta }
    }

    val intersectionsDelta = {
      val created = newKeys(previousState.intersections, intersections)
      val delta =
        deltaKeys(previousState.intersections, intersections, created.keySet)
      delta ++ created
    }
    val roadsDelta = {
      val created = newKeys(previousState.roads, roads)
      val delta =
        deltaKeys(previousState.roads, roads, created.keySet)
      delta ++ created
    }

    val vehiclesDelta = {
      val created = newKeys(previousState.vehicles, vehicles)
      val delta =
        deltaKeys(previousState.vehicles, vehicles, created.keySet)
      delta ++ created
    }

    if (vehiclesDelta.nonEmpty || roadsDelta.nonEmpty || intersectionsDelta.nonEmpty) {
      Some {
        SimulationState(
          this.tickDelta.toMillis,
          this.tickDelta,
          vehiclesDelta,
          roadsDelta,
          intersectionsDelta
        )
      }
    } else None
  }

}

object SimulationState {
  type VehicleId = String
  type RoadId = String
  type IntersectionId = String

  def init: SimulationState =
    SimulationState(totalTicks = 0L,
                    tickDelta = Duration.Zero,
                    vehicles = HashMap.empty,
                    roads = HashMap.empty,
                    intersections = HashMap.empty)
      .copy(vehicles = Map("a1" -> DefaultVehicle("a1", Position())))
}
