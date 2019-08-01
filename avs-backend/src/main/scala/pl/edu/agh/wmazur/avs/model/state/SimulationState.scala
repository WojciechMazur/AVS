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
    currentTime: Long,
    tickDelta: FiniteDuration,
    vehicles: Map[VehicleId, Vehicle],
    roads: Map[RoadId, Road],
    intersections: Map[IntersectionId, Intersection]
)

object SimulationState {
  type VehicleId = Vehicle#Id
  type RoadId = Road#Id
  type IntersectionId = Intersection#Id

  val empty: SimulationState = {
    SimulationState(
      currentTime = 0L,
      tickDelta = Duration.Zero,
      vehicles = HashMap.empty,
      roads = HashMap.empty,
      intersections = HashMap.empty
    )
  }
}
