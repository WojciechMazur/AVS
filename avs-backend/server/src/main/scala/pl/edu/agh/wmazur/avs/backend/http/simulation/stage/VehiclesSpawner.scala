package pl.edu.agh.wmazur.avs.backend.http.simulation.stage
import akka.NotUsed
import akka.stream.scaladsl.Flow
import pl.edu.agh.wmazur.avs.model.entity.road.Lane
import pl.edu.agh.wmazur.avs.model.entity.vehicle.Vehicle
import pl.edu.agh.wmazur.avs.model.state.SimulationState
import com.softwaremill.quicklens._

object VehiclesSpawner extends SimulalationStage {
  override def flow: Flow[SimulationState, SimulationState, NotUsed] = {
    Flow[SimulationState]
      .map { state =>
        val spawnPoints = state.roads.values
          .flatMap(_.lanes)
          .flatMap(_.spawnPoint)

        val spawned: Map[Lane, Vehicle] = spawnPoints.flatMap { spawnPoint =>
          spawnPoint
            .trySpawn(state)
            .map(spawnPoint.lane -> _)
        }(scala.collection.breakOut)

        val updatedVehiclesAtLanes =
          spawned.map {
            case (lane, vehicle) =>
              val vehiclesAtLine = state.vehiclesAtLanes
                .getOrElse(lane, Set.empty) + vehicle.id
              lane -> vehiclesAtLine
          }

        val vehicles = spawned.map {
          case (_, vehicle) =>
            vehicle.id -> vehicle
        }

        state
          .modify(_.vehiclesAtLanes)
          .using(_ ++ updatedVehiclesAtLanes)
          .modify(_.vehicles)
          .using(_ ++ vehicles)
      }
  }
}
