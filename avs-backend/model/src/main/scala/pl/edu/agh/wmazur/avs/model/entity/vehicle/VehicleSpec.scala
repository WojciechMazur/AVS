package pl.edu.agh.wmazur.avs.model.entity.vehicle

import org.locationtech.spatial4j.shape.Point
import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.utils.SpatialUtils.Point2
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{
  Acceleration,
  Angle,
  Velocity
}
import pl.edu.agh.wmazur.avs.model.entity.utils.SpatialUtils.PointUtils

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
  val wheelSpan: Dimension = (width - wheelWidth) / 2.0

  val halfWidth: Dimension = width / 2.0
  val halfLength: Dimension = length / 2.0
  val radius: Dimension = Math.sqrt(
    Math.pow(length.meters, 2) + Math.pow(width.meters, 2)) / 2

  def pointBetweenFrontWheels(position: Point, angle: Angle): Point =
    position.move(-frontAxleDisplacement, angle)

  def pointBetweenBackWheels(position: Point, angle: Angle): Point =
    position.move(rearAxleDisplacement, angle)

}

object VehicleSpec {
  type Acceleration = Double
  type Velocity = Double
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

    lazy val values: Vector[VehicleSpec] = Vector(
      Sedan,
      Coupe,
      Van,
    )
  }

}
