package pl.edu.agh.wmazur.avs.model.entity.vehicle.movement

import org.locationtech.spatial4j.shape.Point
import org.locationtech.spatial4j.shape.impl.PointImpl
import pl.edu.agh.wmazur.avs.model.entity.utils.MathUtils
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{Angle, Velocity}

case class SteeringMovement(vehicleSpec: VehicleSpec,
                            position: Point,
                            heading: Angle,
                            _velocity: Velocity,
                            _steeringAngle: Angle)
    extends VehicleMovement.UniformVehicleMovement {
  val steeringAngle: Angle = MathUtils.withConstraint(
    _steeringAngle,
    -vehicleSpec.maxSteeringAngle,
    vehicleSpec.maxSteeringAngle)

  override def move(tickDelta: TimeDelta): VehicleMovement = {
    if (steeringAngle.abs < VehicleMovement.steeringAngleThreshold) {
      moveStraight(tickDelta)
    } else {
      moveByArc(tickDelta)
    }
  }

  private def moveStraight(timeDelta: TimeDelta): VehicleMovement = {
    val newPosition = new PointImpl(
      position.getX + velocity * Math.cos(heading) * timeDelta,
      position.getX + velocity * Math.sin(heading) * timeDelta
    )
    copy(position = newPosition)
  }

  private def moveByArc(timeDelta: TimeDelta): VehicleMovement = {
    val rotationRate = velocity * Math.tan(
      steeringAngle / vehicleSpec.wheelBase)
    val finalHeading: Angle = MathUtils.boundedAngle {
      heading + rotationRate * timeDelta
    }.toFloat

    val rearWheelPos = vehicleSpec.pointBetweenBackWheels(position, heading)

    val wheelsOffset = vehicleSpec.wheelBase / Math.tan(steeringAngle)
    val xDelta = rearWheelPos.getX - wheelsOffset * (Math.sin(heading) - Math
      .sin(finalHeading))
    val yDelta = position.getY - wheelsOffset * (Math.cos(finalHeading) - Math
      .cos(heading))

    val newPosition = new PointImpl(
      xDelta + vehicleSpec.rearAxleDisplacement * Math.cos(finalHeading),
      yDelta + vehicleSpec.rearAxleDisplacement * Math.sin(finalHeading)
    )

    copy(
      position = newPosition,
      heading = finalHeading
    )
  }
}
