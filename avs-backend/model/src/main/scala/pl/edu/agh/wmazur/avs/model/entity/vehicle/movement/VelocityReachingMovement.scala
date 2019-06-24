package pl.edu.agh.wmazur.avs.model.entity.vehicle.movement
import org.locationtech.spatial4j.shape.Point
import pl.edu.agh.wmazur.avs.model.entity.utils.MathUtils
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{
  Acceleration,
  Angle,
  Velocity
}
import MathUtils._
import com.softwaremill.quicklens._

case class VelocityReachingMovement(
    override val uniformMovement: VehicleMovement.UniformVehicleMovement,
    override val _acceleration: Acceleration,
    _targetVelocity: Velocity
) extends PhysicalMovement(uniformMovement, _acceleration) {
  val targetVelocity: Velocity = MathUtils.withConstraint(
    _targetVelocity,
    vehicleSpec.minVelocity,
    vehicleSpec.maxVelocity) match {
    case v if acceleration.isZero => v
    case _ if acceleration > 0.0  => vehicleSpec.maxVelocity
    case _ if acceleration < 0.0  => vehicleSpec.minVelocity
  }

  def withSteadyVelocity: VelocityReachingMovement = {
    copy(_acceleration = 0, _targetVelocity = velocity)
  }

  def stop: VelocityReachingMovement = {
    velocity match {
      case v if v > 0.0 =>
        copy(_acceleration = vehicleSpec.maxDeceleration, _targetVelocity = 0)
      case v if v < 0.0 =>
        copy(_acceleration = vehicleSpec.maxAcceleration, _targetVelocity = 0)
      case _ => copy(_acceleration = 0, _targetVelocity = 0.0f)
    }
  }

  def maxAccelerationAndTargetVelocity: VelocityReachingMovement = {
    copy(_acceleration = vehicleSpec.maxAcceleration,
         _targetVelocity = vehicleSpec.maxVelocity)
  }

  def maxAccelerationWithTargetVelocity(
      targetVelocity: Velocity): VelocityReachingMovement = {
    if (velocity < targetVelocity) {
      copy(_acceleration = vehicleSpec.maxAcceleration,
           _targetVelocity = targetVelocity)
    } else if (velocity > targetVelocity) {
      copy(_acceleration = vehicleSpec.maxDeceleration,
           _targetVelocity = targetVelocity)
    } else {
      copy(_acceleration = 0)
    }
  }

  def maxVelocityWithAcceleration(
      acceleration: Acceleration): VelocityReachingMovement = {
    if (acceleration > 0) {
      copy(_acceleration = acceleration,
           _targetVelocity = vehicleSpec.maxVelocity)
    } else if (acceleration < 0) {
      copy(_acceleration = acceleration,
           _targetVelocity = vehicleSpec.minVelocity)
    } else {
      copy(_acceleration = acceleration, _targetVelocity = velocity)
    }
  }

  private def moveWithAcceleration(accelerationDelta: Acceleration,
                                   tickDelta: TimeDelta) = {
    this
      .modify(_.uniformMovement.velocity)
      .setTo(velocity + accelerationDelta / 2)
      .moveWithConstantVelocity(tickDelta)
      .asInstanceOf[VelocityReachingMovement]
      .modify(_.uniformMovement.velocity)
      .setTo(velocity + accelerationDelta)
  }

  private def moveWithPartialAcceleration(targetVelocityDelta: Velocity,
                                          timeDelta: TimeDelta,
                                          accelerationDuration: TimeDelta) = {
    this
      .modify(_.uniformMovement.velocity)
      .setTo(velocity + targetVelocityDelta / 2)
      .moveWithConstantVelocity(accelerationDuration)
      .asInstanceOf[VelocityReachingMovement]
      .modify(_.uniformMovement.velocity)
      .setTo(velocity + targetVelocityDelta)
      .moveWithConstantVelocity(timeDelta - accelerationDuration)
  }

  private def moveWhenAccelerating(tickDelta: TimeDelta) = {
    velocity match {
      case v if v >= targetVelocity => moveWithConstantVelocity(tickDelta)
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

  private def moveWhenDeaccelerating(tickDelta: TimeDelta) = {
    velocity match {
      case v if v <= targetVelocity => moveWithConstantVelocity(tickDelta)
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

  override def move(tickDelta: TimeDelta): VehicleMovement =
    acceleration match {
      case acc if acc.isZero => moveWithConstantVelocity(tickDelta)
      case acc if acc > 0    => moveWhenAccelerating(tickDelta)
      case _                 => moveWhenDeaccelerating(tickDelta)
    }
}

object VelocityReachingMovement {
  def apply(
      vehicleSpec: VehicleSpec,
      position: Point,
      heading: Angle,
      velocity: Velocity,
      steeringAngle: Angle,
      acceleration: Acceleration,
      targetVelocity: Velocity
  ): VelocityReachingMovement =
    new VelocityReachingMovement(
      SteeringMovement(vehicleSpec, position, heading, velocity, steeringAngle),
      acceleration,
      targetVelocity
    )
}
