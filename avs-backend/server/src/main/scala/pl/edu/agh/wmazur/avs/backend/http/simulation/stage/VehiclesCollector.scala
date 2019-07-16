package pl.edu.agh.wmazur.avs.backend.http.simulation.stage
import akka.NotUsed
import akka.stream.scaladsl.Flow
import pl.edu.agh.wmazur.avs.model.state.SimulationState
import com.softwaremill.quicklens._

object VehiclesCollector extends SimulalationStage {
  override def flow: Flow[SimulationState, SimulationState, NotUsed] =
    Flow[SimulationState]
      .map { state =>
        val vehiclesToRemove = state.vehiclesAtLanes
          .map {
            case (lane, vehicleIds) =>
              val vehicles = vehicleIds.map(state.vehicles)
              lane -> lane.collectorPoint
                .map(_.checkToRemove(vehicles).toSet)
                .getOrElse(Set.empty)
          }(collection.breakOut)
          .filter { case (_, vehicles) => vehicles.nonEmpty }

        val updatedVehiclesAtLanes =
          vehiclesToRemove.map {
            case (lane, vehicles) =>
              val vehiclesAtLine = state.vehiclesAtLanes
                .getOrElse(lane, Set.empty) -- vehicles.map(_.id)
              lane -> vehiclesAtLine
          }

        val collectedVehicles = vehiclesToRemove
          .flatMap {
            case (_, vehicles) => vehicles
          }
          .map(vehicle => vehicle.id)

        state
          .modify(_.vehiclesAtLanes)
          .using(_ ++ updatedVehiclesAtLanes)
          .modify(_.vehicles)
          .using(_ -- collectedVehicles)
      }

}
