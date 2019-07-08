package pl.edu.agh.wmazur.avs.model.entity.vehicle

import org.locationtech.spatial4j.context.SpatialContext
import org.locationtech.spatial4j.shape.Point
import org.locationtech.spatial4j.shape.impl.PointImpl
import pl.edu.agh.wmazur.avs.model.entity.Entity
import pl.edu.agh.wmazur.avs.model.entity.utils.DeltaOps
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{
  Acceleration,
  Angle,
  Dimension,
  Velocity
}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.movement.VehicleMovement

trait Vehicle extends Entity with DeltaOps[Vehicle] {
  self: VehicleMovement =>

  def spec: VehicleSpec

  def velocity: Velocity

  def acceleration: Acceleration

  def steeringAngle: Angle

  def heading: Angle //in radians

  override def isUpdatedBy(old: Vehicle): Boolean = {
    this.position != old.position ||
    Math.abs(this.acceleration - old.acceleration) > 0.001 ||
    Math.abs(this.velocity - old.velocity) > 0.001
  }

  lazy val pointAtMiddleFront: Point = {
    val precision = 0.0000001
    new PointImpl(
      position.getX + precision * Math.cos(heading),
      position.getY + precision * Math.sin(heading),
      SpatialContext.GEO
    )

  }
}

object Vehicle {
  type Vin = Vehicle#Id
  final val minSteeringThreshold: Angle = 0.0001

}
