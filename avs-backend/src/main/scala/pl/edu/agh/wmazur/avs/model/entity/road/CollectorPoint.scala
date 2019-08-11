package pl.edu.agh.wmazur.avs.model.entity.road

import java.util.concurrent.atomic.AtomicInteger

import org.locationtech.jts.geom.Geometry
import org.locationtech.spatial4j.shape.{Point, Shape}
import pl.edu.agh.wmazur.avs.model.entity.utils.SpatialUtils
import pl.edu.agh.wmazur.avs.model.entity.vehicle.{Vehicle, VehicleSpec}

case class CollectorPoint(lane: Lane) {
  private val collectedCounter = new AtomicInteger(0)
  def collected: Int = collectedCounter.get()
  val collectGeometry: Geometry = {
    val maxVehicleLength =
      VehicleSpec.Predefined.values.map(_.length).maxBy(_.asMeters)

    lane
      .getGeometryFraction(1 - maxVehicleLength.asMeters / lane.length.asMeters,
                           1)
      .buffer(0.000001)
  }
  val collectArea: Shape =
    SpatialUtils.shapeFactory.makeShapeFromGeometry(collectGeometry)

  def shouldBeRemoved(position: Point,
                      area: Option[Shape] = None,
                      geometry: Option[Geometry] = None): Boolean = {
    val positionWithinArea = position.relate(collectArea).intersects()
    lazy val intersectsCollectArea =
      area.exists(_.relate(collectArea).intersects())
    lazy val intersectCollectGeometry =
      geometry.exists(_.relate(collectGeometry).isIntersects)

    positionWithinArea || intersectsCollectArea || intersectCollectGeometry
  }
  def shouldBeRemoved(vehicle: Vehicle): Boolean = {
    shouldBeRemoved(vehicle.position, Some(vehicle.area), None)
  }
  def checkToRemove(vehicles: Iterable[Vehicle]): Iterable[Vehicle] = {
    vehicles.collect {
      case vehicle if shouldBeRemoved(vehicle) =>
        collectedCounter.incrementAndGet()
        vehicle
    }
  }
}
