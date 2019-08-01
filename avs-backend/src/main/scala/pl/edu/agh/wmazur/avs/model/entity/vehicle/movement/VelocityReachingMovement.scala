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
  def targetVelocity: Velocity

  override protected def checkBounds(): VelocityReachingMovement = {
    val boundedTargetVelocity = MathUtils.withConstraint(this.targetVelocity,
                                                         spec.minVelocity,
                                                         spec.maxVelocity)
    val targetVelocity = acceleration match {
      case a if a.isZero => boundedTargetVelocity
      case a if a > 0.0  => spec.maxVelocity
      case a if a < 0.0  => spec.minVelocity
    }

    val self = super.checkBounds().asInstanceOf[VelocityReachingMovement]

    if (this.targetVelocity != targetVelocity) {
      self.withTargetVelocity(targetVelocity)
    } else {
      self
    }
  }

  private def moveWithAcceleration(accelerationDelta: Acceleration,
                                   tickDelta: TimeDeltaSeconds) = {
    this
      .withVelocity(velocity + accelerationDelta / 2)
      .moveWithConstantVelocity(tickDelta)
      .withVelocity(velocity + accelerationDelta)
  }

  private def moveWithPartialAcceleration(
      targetVelocityDelta: Velocity,
      timeDelta: TimeDeltaSeconds,
      accelerationDuration: TimeDeltaSeconds) = {
    this
      .withVelocity(velocity + targetVelocityDelta / 2)
      .moveWithConstantVelocity(accelerationDuration)
      .withVelocity(velocity + targetVelocityDelta)
      .asInstanceOf[this.type]
      .moveWithConstantVelocity(timeDelta - accelerationDuration)
  }

  private def moveWhenAccelerating(tickDelta: TimeDeltaSeconds) = {
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

  private def moveWhenDeaccelerating(tickDelta: TimeDeltaSeconds) = {
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

  override def move(tickDelta: TimeDeltaSeconds): VehicleMovement =
    acceleration match {
      case acc if acc.isZero => moveWithConstantVelocity(tickDelta)
      case acc if acc > 0    => moveWhenAccelerating(tickDelta)
      case _                 => moveWhenDeaccelerating(tickDelta)
    }

  def withSteadyVelocity: VelocityReachingMovement =
    this.withTargetVelocity(velocity).withAcceleration(0)

  def stop: VelocityReachingMovement = {
    val acceleration = velocity match {
      case v if v > 0.0 => spec.maxDeceleration
      case v if v < 0.0 => spec.maxAcceleration
      case _            => 0d
    }
    this.withTargetVelocity(0).withAcceleration(acceleration)
  }

  def maxAccelerationAndTargetVelocity: VelocityReachingMovement =
    this
      .withAcceleration(spec.maxAcceleration)
      .withTargetVelocity(spec.maxVelocity)

  def maxAccelerationWithTargetVelocity(
      targetVelocity: Velocity): VelocityReachingMovement = {
    val acceleration = velocity match {
      case v if v > targetVelocity => spec.maxDeceleration
      case v if v < targetVelocity => spec.maxAcceleration
      case _                       => 0d
    }

    this
      .withTargetVelocity(targetVelocity)
      .withAcceleration(acceleration)
  }

  def maxVelocityWithAcceleration(
      acceleration: Acceleration): VelocityReachingMovement = {
    val targetVelocity = acceleration match {
      case a if a.isZero => velocity
      case a if a > 0    => spec.maxVelocity
      case a if a < 0    => spec.minVelocity
    }
    this
      .withTargetVelocity(targetVelocity)
      .withAcceleration(acceleration)
  }

  def withTargetVelocity(targetVelocity: Velocity): VelocityReachingMovement

  override def withAcceleration(
      acceleration: Acceleration): VelocityReachingMovement
  override def withVelocity(velocity: Velocity): VelocityReachingMovement
  override def withSteeringAngle(steeringAngle: Angle): VelocityReachingMovement
  override def withHeading(heading: Angle): VelocityReachingMovement

  override def withPosition(position: Point): VelocityReachingMovement
}
