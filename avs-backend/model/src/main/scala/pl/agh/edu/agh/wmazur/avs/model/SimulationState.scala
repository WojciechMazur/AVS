package pl.agh.edu.agh.wmazur.avs.model
import pl.agh.edu.agh.wmazur.avs.model.SimulationState._
import pl.agh.edu.agh.wmazur.avs.model.entity.intersection.Intersection
import pl.agh.edu.agh.wmazur.avs.model.entity.road.Road
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
        vehicles = Map("a1" -> DefaultVehicle("a1", Position(), 1.0f),
                       "a2" -> DefaultVehicle("a2", Position(x = 1f), 2.0f),
                       "a3" -> DefaultVehicle("a3", Position(x = 2f), 3.0f))
      )
}
