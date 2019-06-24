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
    wheelSpan: Dimension,
    wheelRadius: Dimension,
    wheelWidth: Dimension,
    maxSteeringAngle: Angle,
    maxTurnPerSecond: Angle,
) {
  //Todo przekazać TickSource.TickDelta
  val maxTurnPerFrame: Angle = maxTurnPerSecond / 60
  val wheelBase: Dimension = rearAxleDisplacement - frontAxleDisplacement
  val halfWidth: Dimension = width / 2
  val halfLength: Dimension = length / 2
  val radius: Dimension =
    (Math.sqrt(Math.pow(length, 2) + Math.pow(width, 2)) / 2).toFloat

  def pointBetweenFrontWheels(position: Point, angle: Angle): Point =
    new PointImpl(
      position.getX - frontAxleDisplacement * Math.cos(angle),
      position.getY - frontAxleDisplacement * Math.sin(angle)
    )

  def pointBetweenBackWheels(position: Point, angle: Angle): Point =
    new PointImpl(
      position.getX - rearAxleDisplacement * Math.cos(angle),
      position.getY - rearAxleDisplacement * Math.sin(angle)
    )

}

object VehicleSpec {
  type Acceleration = Float
  type Velocity = Float
  type Dimension = Float
  type Angle = Float

}
