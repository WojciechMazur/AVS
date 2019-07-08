package pl.edu.agh.wmazur.avs.model.entity.vehicle

import org.locationtech.spatial4j.context.SpatialContext
import org.locationtech.spatial4j.shape.Point
import org.locationtech.spatial4j.shape.impl.PointImpl
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{
  Acceleration,
  Angle,
  Dimension,
  Velocity
}

case class VehicleSpec(
    maxAcceleration: Acceleration,
    maxDeceleration: Acceleration,
    maxVelocity: Velocity,
    minVelocity: Velocity,
    length: Dimension,
    width: Dimension,
    frontAxleDisplacement: Dimension,
    rearAxleDisplacement: Dimension,
    wheelRadius: Dimension,
    wheelWidth: Dimension,
    maxSteeringAngle: Angle,
    maxTurnPerSecond: Angle,
) {
  //Todo przekazać TickSource.TickDelta
  val maxTurnPerFrame: Angle = maxTurnPerSecond / 60
  val wheelBase: Dimension = rearAxleDisplacement - frontAxleDisplacement
  val wheelSpan: Dimension = (width - wheelWidth) / 2

  val halfWidth: Dimension = width / 2
  val halfLength: Dimension = length / 2
  val radius: Dimension =
    (Math.sqrt(Math.pow(length, 2) + Math.pow(width, 2)) / 2).toFloat

  def pointBetweenFrontWheels(position: Point, angle: Angle): Point =
    new PointImpl(
      position.getX - frontAxleDisplacement * Math.cos(angle),
      position.getY - frontAxleDisplacement * Math.sin(angle),
      SpatialContext.GEO
    )

  def pointBetweenBackWheels(position: Point, angle: Angle): Point =
    new PointImpl(
      position.getX - rearAxleDisplacement * Math.cos(angle),
      position.getY - rearAxleDisplacement * Math.sin(angle),
      SpatialContext.GEO
    )

}

object VehicleSpec {
  type Acceleration = Double
  type Velocity = Double
  type Dimension = Double
  type Angle = Double

  object Predefined {
    lazy val Sedan = VehicleSpec(
      maxAcceleration = 3.25,
      maxDeceleration = -39.0,
      maxVelocity = 55.0,
      minVelocity = -15.0,
      length = 5.0,
      width = 1.85,
      frontAxleDisplacement = 1.2,
      rearAxleDisplacement = 4.0,
      wheelRadius = 0.33,
      wheelWidth = 0.25,
      maxSteeringAngle = Math.PI / 3,
      maxTurnPerSecond = Math.PI / 3
    )

    lazy val Coupe = VehicleSpec(
      maxAcceleration = 4.5,
      maxDeceleration = -45.0,
      maxVelocity = 60.0,
      minVelocity = -17.0,
      length = 4.0,
      width = 1.85,
      frontAxleDisplacement = 1.0,
      rearAxleDisplacement = 3.5,
      wheelRadius = 0.3,
      wheelWidth = 0.25,
      maxSteeringAngle = Math.PI / 3,
      maxTurnPerSecond = Math.PI / 2
    )

    lazy val Van = VehicleSpec(
      maxAcceleration = 3.83,
      maxDeceleration = -39.0,
      maxVelocity = 52.0,
      minVelocity = -12.0,
      length = 5.13,
      width = 2.007,
      frontAxleDisplacement = 1.18,
      rearAxleDisplacement = 4.126,
      wheelRadius = 0.375,
      wheelWidth = 0.33,
      maxSteeringAngle = Math.PI / 3,
      maxTurnPerSecond = Math.PI / 3
    )
  }

}
