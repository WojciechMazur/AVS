package pl.edu.agh.wmazur.avs.model.entity.vehicle.movement

import org.locationtech.spatial4j.shape.Point
import pl.edu.agh.wmazur.avs.model.entity.utils.MathUtils
import pl.edu.agh.wmazur.avs.model.entity.utils.SpatialUtils.{
  Point2,
  PointUtils
}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.Angle
import pl.edu.agh.wmazur.avs.model.entity.vehicle.movement.VehicleMovement.TimeDelta
trait SteeringMovement extends VehicleMovement.UniformVehicleMovement {
  def moveWithConstantVelocity(tickDelta: TimeDelta): VehicleMovement = {
    if (steeringAngle.abs < VehicleMovement.steeringAngleThreshold) {
      moveStraight(tickDelta)
    } else {
      moveByArc(tickDelta)
    }
  }

  private def moveStraight(timeDelta: TimeDelta): VehicleMovement = {
    val newPosition = position.moveRotate(velocity * timeDelta, heading)
    withPosition(newPosition)
  }

  private def moveByArc(timeDelta: TimeDelta): VehicleMovement = {
    val rotationRate = velocity * Math.tan(
      steeringAngle / spec.wheelBase.meters)
    val finalHeading: Angle = MathUtils.boundedAngle {
      heading + rotationRate * timeDelta
    }.toFloat

    val rearWheelPos = spec.pointBetweenBackWheels(position, heading)

    val wheelsOffset = spec.wheelBase / Math.tan(steeringAngle)

    val newPosition = Point2(
      x = rearWheelPos.getX - wheelsOffset.geoDegrees *
        (Math.sin(heading) - Math.sin(finalHeading)),
      y = position.getY - wheelsOffset.geoDegrees *
        (Math.cos(finalHeading) - Math.cos(heading))
    ).moveRotate(spec.rearAxleDisplacement, finalHeading)

    this
      .withPosition(newPosition)
      .withHeading(heading)
  }

  def moveWheelsTowardPoint(point: Point): VehicleMovement = {
    val angle = {
      import pl.edu.agh.wmazur.avs.model.entity.utils.SpatialUtils._
      val pointBetweenFrontWheels =
        spec.pointBetweenFrontWheels(position, heading)
      val angleToPoint = pointBetweenFrontWheels.angle(point)

      MathUtils.recenter(
        angleToPoint - heading,
        -Math.PI,
        Math.PI
      )
    }
    this.withSteeringAngle(angle)
  }

}
