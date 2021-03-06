package pl.edu.agh.wmazur.avs.model.entity.vehicle.movement

import mikera.vectorz.Vector2
import org.locationtech.spatial4j.shape.Point
import pl.edu.agh.wmazur.avs.model.entity.utils.MathUtils
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{
  Acceleration,
  Angle,
  Velocity
}
import pl.edu.agh.wmazur.avs.model.entity.utils.SpatialUtils._
import pl.edu.agh.wmazur.avs.model.entity.vehicle.movement.VehicleMovement.TimeDeltaSeconds
import pl.edu.agh.wmazur.avs.model.entity.vehicle.{Vehicle, VehicleSpec}
import pl.edu.agh.wmazur.avs.model.entity.intersection.reservation.ReservationArray.Timestamp

import scala.concurrent.duration.FiniteDuration

sealed trait VehicleMovement {
  self: Vehicle =>
  def position: Point
  def heading: Angle
  def velocity: Velocity
  def acceleration: Acceleration
  def steeringAngle: Angle
  def spec: VehicleSpec

  val positionVector: Vector2 = position

  def move(tickDelta: TimeDeltaSeconds): self.type

  def withAcceleration(acceleration: Acceleration): self.type
  def withVelocity(velocity: Velocity): self.type
  def withSteeringAngle(steeringAngle: Angle): self.type
  def withPositionAndHeading(position: Point, heading: Angle): self.type
  def withHeading(heading: Angle): self.type
  def withPosition(position: Point): self.type
}

object VehicleMovement {
  type TimeDeltaSeconds = Double

  val steeringAngleThreshold: Angle = 0.0001f

  trait UniformVehicleMovement extends VehicleMovement {
    self: Vehicle =>
    def moveWithConstantVelocity(timeDelta: TimeDeltaSeconds): self.type
  }
  trait VariableVehicleMovement extends VehicleMovement {
    self: Vehicle =>
    def moveWithAcceleration(timeDelta: TimeDeltaSeconds): self.type
  }

  trait ScheduledVehicleMovement extends VehicleMovement {
    self: Vehicle with VariableVehicleMovement =>
    def moveWithSchedule(currentTime: Timestamp,
                         timeDelta: TimeDeltaSeconds): self.type
  }

}
