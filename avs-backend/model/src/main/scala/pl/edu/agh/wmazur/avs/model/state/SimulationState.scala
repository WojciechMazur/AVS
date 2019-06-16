package pl.edu.agh.wmazur.avs.model.state

import com.github.jpbetz.subspace.Vector3
import pl.edu.agh.wmazur.avs.model.entity.intersection.Intersection
import pl.edu.agh.wmazur.avs.model.entity.road.Road
import pl.edu.agh.wmazur.avs.model.entity.vehicle.{DefaultVehicle, Vehicle}
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
  type VehicleId = String
  type RoadId = String
  type IntersectionId = String

  val init: SimulationState =
    SimulationState(totalTicks = 0L,
                    tickDelta = Duration.Zero,
                    vehicles = HashMap.empty,
                    roads = HashMap.empty,
                    intersections = HashMap.empty)
      .copy(
        vehicles = Map("a1" -> DefaultVehicle("a1", Vector3.fill(0), 1.0f),
                       "a2" -> DefaultVehicle("a2", Vector3(1f, 0, 0), 2.0f),
                       "a3" -> DefaultVehicle("a3", Vector3(2f, 0, 0), 3.0f))
      )
}
