package pl.edu.agh.wmazur.avs.model.entity.vehicle.movement

import mikera.vectorz.Vector2
import org.locationtech.spatial4j.shape.Point
import pl.edu.agh.wmazur.avs.model.entity.utils.MathUtils
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{
  Acceleration,
  Angle,
  Velocity
}
import pl.edu.agh.wmazur.avs.model.entity.utils.SpatialUtils._
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec
sealed trait VehicleMovement {
  type TimeDelta = Double
  def position: Point
  def heading: Angle
  def velocity: Velocity

  val positionVector: Vector2 = position

  def move(tickDelta: TimeDelta): VehicleMovement
}

object VehicleMovement {
  val steeringAngleThreshold: Angle = 0.0001f

  trait UniformVehicleMovement extends VehicleMovement {
    def vehicleSpec: VehicleSpec

    protected val _velocity: Velocity
    val velocity: Velocity = _velocity match {
      case v if v > vehicleSpec.maxVelocity => vehicleSpec.maxVelocity
      case v if v < vehicleSpec.minVelocity => vehicleSpec.minVelocity
      case v                                => v
    }
  }

  trait VariableVehicleMovement extends VehicleMovement {
    def vehicleSpec: VehicleSpec

    val _acceleration: Acceleration
    val acceleration: Acceleration =
      MathUtils.withConstraint(_acceleration,
                               vehicleSpec.maxDeceleration,
                               vehicleSpec.maxAcceleration)
  }

}
