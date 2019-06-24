package pl.edu.agh.wmazur.avs.model.entity.vehicle.movement

import org.locationtech.spatial4j.shape.Point
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec
import pl.edu.agh.wmazur.avs.model.entity.vehicle.movement.VehicleMovement.UniformVehicleMovement
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{
  Acceleration,
  Angle,
  Velocity
}
import pl.edu.agh.wmazur.avs.model.entity.utils.MathUtils._

class PhysicalMovement(val uniformMovement: UniformVehicleMovement,
                       val _acceleration: Acceleration)
    extends VehicleMovement.VariableVehicleMovement {
  override def vehicleSpec: VehicleSpec = uniformMovement.vehicleSpec

  override def position: Point = uniformMovement.position
  override def heading: Angle = uniformMovement.heading
  override def velocity: Velocity = uniformMovement.velocity

  def copy(uniformMovement: UniformVehicleMovement = this.uniformMovement,
           acceleration: Acceleration = this.acceleration): PhysicalMovement =
    new PhysicalMovement(uniformMovement, acceleration)

  def moveWithConstantVelocity(tickDelta: TimeDelta): VehicleMovement = {
    uniformMovement.move(tickDelta)
  }

  override def move(tickDelta: TimeDelta): VehicleMovement = {
    if (acceleration.isZero) {
      moveWithConstantVelocity(tickDelta)
    } else {
      val velocityDelta = acceleration * tickDelta
      val initialVelocity = uniformMovement.velocity
      copy(acceleration = initialVelocity + velocityDelta / 2)
        .move(tickDelta)
        .asInstanceOf[PhysicalMovement]
        .copy(acceleration = initialVelocity + velocityDelta)
    }
  }
}
