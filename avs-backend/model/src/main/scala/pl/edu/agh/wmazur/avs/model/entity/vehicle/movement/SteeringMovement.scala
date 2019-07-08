package pl.edu.agh.wmazur.avs.model.entity.vehicle.movement

import org.locationtech.spatial4j.context.SpatialContext
import org.locationtech.spatial4j.shape.Point
import org.locationtech.spatial4j.shape.impl.PointImpl
import pl.edu.agh.wmazur.avs.model.entity.utils.MathUtils
import pl.edu.agh.wmazur.avs.model.entity.vehicle.Vehicle
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
    val newPosition = new PointImpl(
      position.getX + velocity * Math.cos(heading) * timeDelta,
      position.getX + velocity * Math.sin(heading) * timeDelta,
      SpatialContext.GEO
    )
    withPosition(newPosition)
  }

  private def moveByArc(timeDelta: TimeDelta): VehicleMovement = {
    val rotationRate = velocity * Math.tan(steeringAngle / spec.wheelBase)
    val finalHeading: Angle = MathUtils.boundedAngle {
      heading + rotationRate * timeDelta
    }.toFloat

    val rearWheelPos = spec.pointBetweenBackWheels(position, heading)

    val wheelsOffset = spec.wheelBase / Math.tan(steeringAngle)
    val xDelta = rearWheelPos.getX - wheelsOffset * (Math.sin(heading) - Math
      .sin(finalHeading))
    val yDelta = position.getY - wheelsOffset * (Math.cos(finalHeading) - Math
      .cos(heading))

    val newPosition = new PointImpl(
      xDelta + spec.rearAxleDisplacement * Math.cos(finalHeading),
      yDelta + spec.rearAxleDisplacement * Math.sin(finalHeading),
      SpatialContext.GEO
    )

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
