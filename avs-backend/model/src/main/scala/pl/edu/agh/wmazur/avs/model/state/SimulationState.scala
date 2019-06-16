package pl.edu.agh.wmazur.avs.model.state

import pl.edu.agh.wmazur.avs.model.entity.intersection.Intersection
import pl.edu.agh.wmazur.avs.model.entity.road.Road
import pl.edu.agh.wmazur.avs.model.entity.vehicle.{DefaultVehicle, Vehicle}
import pl.edu.agh.wmazur.avs.model.state.SimulationState.{
  IntersectionId,
  RoadId,
  VehicleId
}
import protobuf.pl.edu.agh.wmazur.avs.model.common.Vector3

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
        vehicles = Map("a1" -> DefaultVehicle("a1", Vector3(), 1.0f),
                       "a2" -> DefaultVehicle("a2", Vector3(x = 1f), 2.0f),
                       "a3" -> DefaultVehicle("a3", Vector3(x = 2f), 3.0f))
      )
}
