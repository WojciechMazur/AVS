package pl.edu.agh.wmazur.avs.model.entity.vehicle.movement
import org.locationtech.spatial4j.shape.Point
import pl.edu.agh.wmazur.avs.model.entity.utils.MathUtils
import pl.edu.agh.wmazur.avs.model.entity.utils.MathUtils._
import pl.edu.agh.wmazur.avs.model.entity.vehicle.Vehicle
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{
  Acceleration,
  Angle,
  Velocity
}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.movement.VehicleMovement.TimeDeltaSeconds

trait VelocityReachingMovement
    extends AcceleratingMovement
    with SteeringMovement {
  self: Vehicle =>
  def targetVelocity: Velocity

  private def moveWithAcceleration(accelerationDelta: Acceleration,
                                   tickDelta: TimeDeltaSeconds): self.type = {
    this
      .withVelocity(velocity + accelerationDelta / 2)
      .moveWithConstantVelocity(tickDelta)
      .withVelocity(velocity + accelerationDelta)
  }

  private def moveWithPartialAcceleration(
      targetVelocityDelta: Velocity,
      timeDelta: TimeDeltaSeconds,
      accelerationDuration: TimeDeltaSeconds): self.type = {
    this
      .withVelocity(velocity + targetVelocityDelta / 2)
      .moveWithConstantVelocity(accelerationDuration)
      .withVelocity(velocity + targetVelocityDelta)
      .asInstanceOf[this.type]
      .moveWithConstantVelocity(timeDelta - accelerationDuration)
  }

  private def moveWhenAccelerating(tickDelta: TimeDeltaSeconds): self.type = {
    velocity match {
      case v if v >= targetVelocity =>
        moveWithConstantVelocity(tickDelta)
      case _ =>
        val accelerationDelta = acceleration * tickDelta
        val targetVelocityDelta = targetVelocity - velocity

        if (targetVelocityDelta >= accelerationDelta) {
          moveWithAcceleration(accelerationDelta, tickDelta)
        } else {
          val accelerationDuration = targetVelocityDelta / acceleration
          moveWithPartialAcceleration(targetVelocityDelta,
                                      tickDelta,
                                      accelerationDuration)
        }
    }
  }

  private def moveWhenDeaccelerating(tickDelta: TimeDeltaSeconds): self.type = {
    velocity match {
      case v if v <= targetVelocity =>
        moveWithConstantVelocity(tickDelta)
      case _ =>
        val accelerationDelta = acceleration * tickDelta
        val targetVelocityDelta = targetVelocity - velocity

        if (targetVelocityDelta <= accelerationDelta) {
          moveWithAcceleration(accelerationDelta, tickDelta)
        } else {
          val accelerationDuration = targetVelocityDelta / acceleration
          moveWithPartialAcceleration(targetVelocityDelta,
                                      tickDelta,
                                      accelerationDuration)
        }
    }
  }

  override def move(duration: TimeDeltaSeconds): self.type = {
    acceleration match {
      case acc if acc.isZero => moveWithConstantVelocity(duration)
      case acc if acc > 0    => moveWhenAccelerating(duration)
      case _                 => moveWhenDeaccelerating(duration)
    }
  }

  def withSteadyVelocity: self.type =
    this.withTargetVelocity(velocity).withAcceleration(0)

  def stop: self.type = {
    val acceleration = velocity match {
      case v if v > 0.0 => spec.maxDeceleration
      case v if v < 0.0 => spec.maxAcceleration
      case _            => 0d
    }
    this.withTargetVelocity(0).withAcceleration(acceleration)
  }

  def maxAccelerationAndTargetVelocity: self.type =
    this
      .withAcceleration(spec.maxAcceleration)
      .withTargetVelocity(spec.maxVelocity)

  def maxAccelerationWithTargetVelocity(targetVelocity: Velocity): self.type = {
    val acceleration = velocity match {
      case v if v > targetVelocity => spec.maxDeceleration
      case v if v < targetVelocity => spec.maxAcceleration
      case _                       => 0d
    }

    this
      .withTargetVelocity(targetVelocity)
      .withAcceleration(acceleration)
  }

  def maxVelocityWithAcceleration(acceleration: Acceleration): self.type = {
    val targetVelocity = acceleration match {
      case a if a.isZero => velocity
      case a if a > 0    => spec.maxVelocity
      case a if a < 0    => spec.minVelocity
    }
    this
      .withTargetVelocity(targetVelocity)
      .withAcceleration(acceleration)
  }

  def withTargetVelocity(targetVelocity: Velocity): self.type

  override def withAcceleration(acceleration: Acceleration): self.type
  override def withVelocity(velocity: Velocity): self.type
  override def withSteeringAngle(steeringAngle: Angle): self.type
  override def withHeading(heading: Angle): self.type
  override def withPosition(position: Point): self.type
}
