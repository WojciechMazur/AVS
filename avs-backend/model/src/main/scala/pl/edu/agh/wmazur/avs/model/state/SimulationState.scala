package pl.edu.agh.wmazur.avs.model.state

import pl.edu.agh.wmazur.avs.model.entity.intersection.Intersection
import pl.edu.agh.wmazur.avs.model.entity.road.{
  DirectedLane,
  Lane,
  LaneSpec,
  Road
}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.Vehicle
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.VehicleDriver
import pl.edu.agh.wmazur.avs.model.state.SimulationState.{
  IntersectionId,
  RoadId,
  VehicleId
}

import scala.collection.immutable.{HashMap, Map}
import scala.concurrent.duration._

case class SimulationState(
    currentTime: Int,
    tickDelta: FiniteDuration,
    vehicles: Map[VehicleId, Vehicle],
    drivers: Map[VehicleId, VehicleDriver],
    vehiclesAtLanes: Map[Lane, Set[VehicleId]],
    roads: Map[RoadId, Road],
    intersections: Map[IntersectionId, Intersection]
)

object SimulationState {
  type VehicleId = Vehicle#Id
  type RoadId = Road#Id
  type IntersectionId = Intersection#Id

  val init: SimulationState = {
    val laneSpec = new LaneSpec(30, 2.5)
    val lane11 =
      DirectedLane.simple(spec = laneSpec, offStartX = -250.0, length = 500.0)

    val lane12 = DirectedLane.simple(spec = laneSpec.copy(speedLimit = 10),
                                     offStartX = -250.0,
                                     offStartY = laneSpec.width + 0.5,
                                     length = 500.0,
    )
    val lane21 = DirectedLane.simple(spec = laneSpec,
                                     offStartY = -100.0,
                                     length = 200.0,
                                     heading = Math.PI / 2)

    val road1 = Road(lane11 :: lane12 :: Nil)
    val road2 = Road(lane21 :: Nil)

    val roads: HashMap[Road#Id, Road] = HashMap(
      road1.id -> road1,
      road2.id -> road2
    )

    SimulationState(
      currentTime = 0,
      tickDelta = Duration.Zero,
      vehicles = HashMap.empty,
      vehiclesAtLanes = Map.empty,
      drivers = HashMap.empty,
      roads = roads,
      intersections = HashMap.empty
    )
  }
}
