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
import pl.edu.agh.wmazur.avs.model.entity.vehicle.movement.VehicleMovement.TimeDelta
import pl.edu.agh.wmazur.avs.model.entity.vehicle.{Vehicle, VehicleSpec}

import scala.concurrent.duration.FiniteDuration

sealed trait VehicleMovement {
  def position: Point
  def heading: Angle
  def velocity: Velocity
  def acceleration: Acceleration
  def steeringAngle: Angle
  def spec: VehicleSpec

  val positionVector: Vector2 = position

  def move(tickDelta: TimeDelta): VehicleMovement

  def withAcceleration(acceleration: Acceleration): VehicleMovement
  def withVelocity(velocity: Velocity): VehicleMovement
  def withSteeringAngle(steeringAngle: Angle): VehicleMovement
  def withHeading(heading: Angle): VehicleMovement
  def withPosition(position: Point): VehicleMovement

  protected def checkBounds(): VehicleMovement = {
    val velocity = MathUtils.withConstraint(this.velocity,
                                            spec.minVelocity,
                                            spec.maxVelocity)
    val acceleration = MathUtils.withConstraint(this.acceleration,
                                                spec.maxDeceleration,
                                                spec.maxAcceleration)
    val steeringAngle = MathUtils.withConstraint(this.steeringAngle,
                                                 -spec.maxSteeringAngle,
                                                 spec.maxSteeringAngle)

    if (velocity != this.velocity || acceleration != this.acceleration || steeringAngle != this.steeringAngle) {
      this
        .withVelocity(velocity)
        .withAcceleration(acceleration)
        .withSteeringAngle(steeringAngle)
    } else { this }
  }

}

object VehicleMovement {
  type TimeDelta = Double

  val steeringAngleThreshold: Angle = 0.0001f

  trait UniformVehicleMovement extends VehicleMovement {
    def moveWithConstantVelocity(timeDelta: TimeDelta): VehicleMovement
  }
  trait VariableVehicleMovement extends VehicleMovement {
    def moveWithAcceleration(timeDelta: TimeDelta): VehicleMovement
  }

}
