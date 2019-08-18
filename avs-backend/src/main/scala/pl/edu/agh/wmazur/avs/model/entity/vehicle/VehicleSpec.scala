package pl.edu.agh.wmazur.avs.model.entity.vehicle

import org.locationtech.spatial4j.shape.Point
import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.utils.IdProvider
import pl.edu.agh.wmazur.avs.model.entity.utils.SpatialUtils.PointUtils
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{
  Acceleration,
  Angle,
  VehicleSpecId,
  Velocity
}
import pl.edu.agh.wmazur.avs.simulation.TickSource

case class VehicleSpec(
    maxAcceleration: Acceleration,
    maxDeceleration: Acceleration,
    maxVelocity: Velocity,
    minVelocity: Velocity,
    length: Dimension,
    width: Dimension,
    height: Dimension,
    frontAxleDisplacement: Dimension,
    rearAxleDisplacement: Dimension,
    wheelRadius: Dimension,
    wheelWidth: Dimension,
    maxSteeringAngle: Angle,
    maxTurnPerSecond: Angle,
    id: VehicleSpecId = VehicleSpec.nextId
) {
  val maxTurnPerFrame: Angle = maxTurnPerSecond / TickSource.timeStepSeconds
  val wheelBase: Dimension = rearAxleDisplacement - frontAxleDisplacement
  val wheelSpan: Dimension = (width - wheelWidth) / 2.0

  val halfWidth: Dimension = width / 2.0
  val halfLength: Dimension = length / 2.0
  val radius: Dimension = Math.sqrt(
    Math.pow(length.asMeters, 2) + Math.pow(width.asMeters, 2)) / 2

  def pointBetweenFrontWheels(position: Point, angle: Angle): Point =
    position.moveRotate(-frontAxleDisplacement, angle)

  def pointBetweenBackWheels(position: Point, angle: Angle): Point =
    position.moveRotate(-rearAxleDisplacement, angle)

}

object VehicleSpec extends IdProvider[VehicleSpec] {
  type VehicleSpecId = Long
  type Acceleration = Double
  type Velocity = Double
  type Angle = Double

  object Predefined {
    lazy val Sedan = VehicleSpec(
      maxAcceleration = 2 * 9.81, //8.25,
      maxDeceleration = -11.5,
      maxVelocity = 55.0,
      minVelocity = -15.0,
      length = 5.0.meters,
      width = 1.85.meters,
      height = 1.5.meters,
      frontAxleDisplacement = 1.2.meters,
      rearAxleDisplacement = 4.0.meters,
      wheelRadius = 0.33.meters,
      wheelWidth = 0.25.meters,
      maxSteeringAngle = Math.PI / 3,
      maxTurnPerSecond = Math.PI / 3
    )

    lazy val Coupe = VehicleSpec(
      maxAcceleration = 4.5,
      maxDeceleration = -10.5,
      maxVelocity = 50.0,
      minVelocity = -17.0,
      length = 4.0.meters,
      width = 1.85.meters,
      height = 1.65.meters,
      frontAxleDisplacement = 1.0.meters,
      rearAxleDisplacement = 3.5.meters,
      wheelRadius = 0.3.meters,
      wheelWidth = 0.25.meters,
      maxSteeringAngle = Math.PI / 3,
      maxTurnPerSecond = Math.PI / 2
    )

    lazy val Van = VehicleSpec(
      maxAcceleration = 3.83,
      maxDeceleration = -9.0,
      maxVelocity = 35.0,
      minVelocity = -12.0,
      length = 5.13,
      width = 2.007,
      height = 1.9,
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
