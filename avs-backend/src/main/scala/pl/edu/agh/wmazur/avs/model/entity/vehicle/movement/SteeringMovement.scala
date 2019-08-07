package pl.edu.agh.wmazur.avs.model.entity.vehicle.movement

import org.locationtech.spatial4j.shape.Point
import pl.edu.agh.wmazur.avs.model.entity.utils.MathUtils
import pl.edu.agh.wmazur.avs.model.entity.utils.SpatialUtils.{
  Point2,
  PointUtils
}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.Vehicle
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.Angle
import pl.edu.agh.wmazur.avs.model.entity.vehicle.movement.VehicleMovement.TimeDeltaSeconds

trait SteeringMovement extends VehicleMovement.UniformVehicleMovement {
  self: Vehicle =>
  def moveWithConstantVelocity(tickDelta: TimeDeltaSeconds): self.type = {
    if (steeringAngle.abs < VehicleMovement.steeringAngleThreshold) {
      moveStraight(tickDelta)
    } else {
      moveByArc(tickDelta)
    }
  }

  private def moveStraight(timeDelta: TimeDeltaSeconds): self.type = {
    val newPosition = position.moveRotate(velocity * timeDelta, heading)
    withPosition(newPosition)
  }

  private def moveByArc(timeDelta: TimeDeltaSeconds): self.type = {
    val rotationRate = velocity * Math.tan(
      steeringAngle / spec.wheelBase.asMeters)
    val finalHeading: Angle = MathUtils.boundedAngle {
      heading + rotationRate * timeDelta
    }.toFloat

    val rearWheelPos = spec.pointBetweenBackWheels(position, heading)

    val wheelsOffset = spec.wheelBase / Math.tan(steeringAngle)

    val newPosition = Point2(
      x = rearWheelPos.getX - wheelsOffset.asGeoDegrees *
        (Math.sin(heading) - Math.sin(finalHeading)),
      y = position.getY - wheelsOffset.asGeoDegrees *
        (Math.cos(finalHeading) - Math.cos(heading))
    ).moveRotate(spec.rearAxleDisplacement, finalHeading)

    this
      .withPosition(newPosition)
      .withHeading(heading)
  }

  def moveWheelsTowardPoint(point: Point): self.type = {
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
    withSteeringAngle(angle)
  }

}
