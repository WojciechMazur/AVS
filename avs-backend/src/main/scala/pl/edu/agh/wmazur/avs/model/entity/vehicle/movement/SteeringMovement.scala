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
import pl.edu.agh.wmazur.avs.Dimension

trait SteeringMovement extends VehicleMovement.UniformVehicleMovement {
  self: Vehicle =>
  def moveWithConstantVelocity(duration: TimeDeltaSeconds): self.type = {
    if (steeringAngle.abs < VehicleMovement.steeringAngleThreshold) {
      moveStraight(duration)
    } else {
      moveByArc(duration)
    }
  }

  private def moveStraight(timeDelta: TimeDeltaSeconds): self.type = {
    val distance = (velocity * timeDelta).meters
    val newPosition = position.moveRotate(distance, heading)
    withPosition(newPosition)
  }

  private def moveByArc(timeDelta: TimeDeltaSeconds): self.type = {
    val rotationRate = velocity * Math.tan(steeringAngle) / spec.wheelBase.asMeters
    val finalHeading: Angle = MathUtils.boundedAngle {
      heading + rotationRate * timeDelta
    }

    val rearWheelPos = spec.pointBetweenBackWheels(position, heading)

    val wheelsOffset = (spec.wheelBase / Math.tan(steeringAngle)).asGeoDegrees

    val newPosition = Point2(
      x = rearWheelPos.getX - (wheelsOffset * (Math.sin(heading) - Math.sin(
        finalHeading))),
      y = rearWheelPos.getY - (wheelsOffset * (Math.cos(finalHeading) - Math
        .cos(heading)))
    ).moveRotate(spec.rearAxleDisplacement, finalHeading)

    this
      .withPosition(newPosition)
      .withHeading(finalHeading)
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
