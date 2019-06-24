package pl.edu.agh.wmazur.avs.model.state

import pl.edu.agh.wmazur.avs.model.entity.intersection.Intersection
import pl.edu.agh.wmazur.avs.model.entity.road.Road
import pl.edu.agh.wmazur.avs.model.entity.vehicle.Vehicle
import pl.edu.agh.wmazur.avs.model.state.SimulationState.{
  IntersectionId,
  RoadId,
  VehicleId
}

import scala.collection.immutable.{HashMap, Map}
import scala.concurrent.duration._

case class SimulationState(
    totalTicks: Long,
    tickDelta: FiniteDuration,
    vehicles: Map[VehicleId, Vehicle],
    roads: Map[RoadId, Road],
    intersections: Map[IntersectionId, Intersection]
)

object SimulationState {
  type VehicleId = Long
  type RoadId = Long
  type IntersectionId = Long

  val init: SimulationState =
    SimulationState(totalTicks = 0L,
                    tickDelta = Duration.Zero,
                    vehicles = HashMap.empty,
                    roads = HashMap.empty,
                    intersections = HashMap.empty)
}
