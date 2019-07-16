package pl.edu.agh.wmazur.avs.model.entity.road

import java.util.concurrent.atomic.AtomicInteger

import org.locationtech.spatial4j.context.SpatialContext
import pl.edu.agh.wmazur.avs.model.entity.vehicle.Vehicle

case class CollectorPoint(lane: Lane) {
  private val collectedCounter = new AtomicInteger(0)
  def collected: Int = collectedCounter.get()

  def checkToRemove(vehicles: Iterable[Vehicle]): Iterable[Vehicle] = {
    vehicles.collect {
      case vehicle
          if !vehicle.area.relate(lane.area).intersects() &&
            SpatialContext.GEO.calcDistance(vehicle.position, lane.exitPoint) < SpatialContext.GEO
              .calcDistance(vehicle.position, lane.entryPoint) =>
        println(s"Marking vehicle ${vehicle.id} to removal")
        collectedCounter.incrementAndGet()
        vehicle
    }
  }
}
