package pl.edu.agh.wmazur.avs.backend.http.simulation.stage
import java.util.concurrent.TimeUnit

import akka.NotUsed
import akka.stream.scaladsl.Flow
import org.locationtech.spatial4j.context.SpatialContext
import org.locationtech.spatial4j.distance.DistanceUtils
import pl.edu.agh.wmazur.avs.model.entity.vehicle.{BasicVehicle, Vehicle}
import pl.edu.agh.wmazur.avs.model.state.SimulationState

object DriversExecutor extends SimulalationStage {
  override def flow: Flow[SimulationState, SimulationState, NotUsed] =
    Flow[SimulationState]
      .map { state =>
        val updatedVehicles: Map[Vehicle.Vin, Vehicle] =
          state.vehicles.values.map {
            case v: BasicVehicle =>
              val updatedVehicle = v
                .move {
                  state.tickDelta.toUnit(TimeUnit.SECONDS)
                }
                .asInstanceOf[Vehicle]
              println(s"Moved vehicle ${v.id} by ${SpatialContext.GEO
                .calcDistance(v.position, updatedVehicle.position) * DistanceUtils.DEG_TO_KM * 1000} m")
              v.id -> updatedVehicle
            case _ => throw new RuntimeException("Unknown type of vehicle")
          }(collection.breakOut)
        state.copy(vehicles = updatedVehicles)
      }
}
