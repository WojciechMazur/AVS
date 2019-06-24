package pl.edu.agh.wmazur.avs.model.entity.vehicle

import org.locationtech.spatial4j.context.SpatialContext
import pl.edu.agh.wmazur.avs.model.entity.Entity
import pl.edu.agh.wmazur.avs.model.entity.utils.DeltaOps
import org.locationtech.spatial4j.shape._
import org.locationtech.spatial4j.shape.impl.PointImpl
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.Angle

trait Vehicle extends Entity with DeltaOps[Vehicle] {
  def spec: VehicleSpec
  def speed: Float
  def acceleration: Float

  def heading: Angle //in radians
  def area: Rectangle
  lazy val centerPoint: Point = area.getCenter

  override def isUpdatedBy(old: Vehicle): Boolean = {
    this.position != old.position ||
    Math.abs(this.acceleration - old.acceleration) > 0.001 ||
    Math.abs(this.speed - old.speed) > 0.001
  }

}

object Vehicle {
  final val minSteeringThreshold: Angle = 0.0001f

}
