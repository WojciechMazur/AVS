package pl.edu.agh.wmazur.avs.model.entity.road

import java.util.concurrent.atomic.AtomicInteger

import org.locationtech.spatial4j.shape.{Point, Shape}
import pl.edu.agh.wmazur.avs.model.entity.utils.SpatialUtils
import pl.edu.agh.wmazur.avs.model.entity.vehicle.{Vehicle, VehicleSpec}

case class CollectorPoint(lane: Lane) {
  private val collectedCounter = new AtomicInteger(0)
  def collected: Int = collectedCounter.get()

  val collectArea: Shape = SpatialUtils.shapeFactory.makeShapeFromGeometry {
    val maxVehicleLength =
      VehicleSpec.Predefined.values.map(_.length).maxBy(_.meters)

    lane
      .getGeometryFraction(1 - maxVehicleLength.meters / lane.length.meters, 1)
      .buffer(0.000001)
  }

  def shouldBeRemoved(position: Point, area: Shape): Boolean = {
    val positionWithinArea = position.relate(collectArea).intersects()
    lazy val intersectsCollectArea = area.relate(collectArea).intersects()
//    def distanceFromEnd: Double =
//      SpatialContext.GEO
//        .calcDistance(position, lane.exitPoint)
//    def distanceFromStart: Double =
//      SpatialContext.GEO.calcDistance(position, lane.entryPoint)
    positionWithinArea || intersectsCollectArea
  }
  def shouldBeRemoved(vehicle: Vehicle): Boolean = {
    shouldBeRemoved(vehicle.position, vehicle.area)
  }
  def checkToRemove(vehicles: Iterable[Vehicle]): Iterable[Vehicle] = {
    vehicles.collect {
      case vehicle if shouldBeRemoved(vehicle) =>
        println(s"Marking vehicle ${vehicle.id} to removal")
        collectedCounter.incrementAndGet()
        vehicle
    }
  }
}
