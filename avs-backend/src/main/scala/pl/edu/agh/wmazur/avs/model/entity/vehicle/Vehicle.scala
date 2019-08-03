package pl.edu.agh.wmazur.avs.model.entity.vehicle

import org.locationtech.spatial4j.shape.Point
import pl.edu.agh.wmazur.avs.model.entity.Entity
import pl.edu.agh.wmazur.avs.model.entity.utils.SpatialUtils.PointUtils
import pl.edu.agh.wmazur.avs.model.entity.utils.{DeltaOps, IdProvider}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{
  Acceleration,
  Angle,
  Velocity
}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.VehicleDriver
import pl.edu.agh.wmazur.avs.model.entity.vehicle.movement.VehicleMovement

trait Vehicle extends Entity with DeltaOps[Vehicle] {
  self: VehicleMovement =>
//  assert(driver.vehicle == this, "Detected driver with invalid vehicle")

  def spec: VehicleSpec
  def gauges: VehicleGauges

  override def position: Point = gauges.position
  override def heading: Angle = gauges.heading
  override def velocity: Velocity = gauges.velocity
  override def acceleration: Acceleration = gauges.acceleration
  override def steeringAngle: Angle = gauges.steeringAngle

  lazy val cornerPoints: List[Point] = {
    val halfPi = Math.PI / 2
    val p1 = position.moveRotate(spec.halfWidth, heading + halfPi / 2)
    val p3 = position.moveRotate(spec.halfWidth, heading - halfPi / 2)
    val p2 = p1.moveRotate(spec.length, heading + Math.PI)
    val p4 = p3.moveRotate(spec.length, heading - Math.PI)

    p3 :: p1 :: p2 :: p4 :: Nil
  }

  override def isUpdatedBy(old: Vehicle): Boolean = {
    this.position != old.position ||
    Math.abs(this.acceleration - old.acceleration) > 0.001 ||
    Math.abs(this.velocity - old.velocity) > 0.001
  }

  lazy val pointAtMiddleFront: Point = {
    val precision = 0.0000001
    position.moveRotate(precision, heading)
  }

}

object Vehicle extends IdProvider[Vehicle] {
  type Vin = Vehicle#Id
  final val minSteeringThreshold: Angle = 0.0001

}
