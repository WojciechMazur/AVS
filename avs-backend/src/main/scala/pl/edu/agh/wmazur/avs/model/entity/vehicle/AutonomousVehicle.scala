package pl.edu.agh.wmazur.avs.model.entity.vehicle

import akka.actor.typed.ActorRef
import com.softwaremill.quicklens._
import org.locationtech.spatial4j.shape.Point
import pl.edu.agh.wmazur.avs.model.entity.utils.MathUtils
import pl.edu.agh.wmazur.avs.model.entity.vehicle.Vehicle.Vin
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{
  Acceleration,
  Angle,
  Velocity
}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousVehicleDriver

case class AutonomousVehicle(
    id: Vin,
    gauges: VehicleGauges,
    spec: VehicleSpec,
    targetVelocity: Velocity,
    driverRef: ActorRef[AutonomousVehicleDriver.ExtendedProtocol],
    spawnTime: Int,
    accelerationSchedule: Option[AccelerationSchedule] = None
) extends BasicVehicle(id, gauges, spec, targetVelocity, spawnTime) {

  override def withAcceleration(
      acceleration: Acceleration): AutonomousVehicle.this.type = {
    modify(this)(_.gauges.acceleration)
      .setTo(acceleration)
      .asInstanceOf[this.type]
  }
  override def withVelocity(velocity: Velocity): AutonomousVehicle.this.type =
    modify(this)(_.gauges.velocity)
      .setTo(velocity)
      .asInstanceOf[this.type]

  override def withSteeringAngle(
      steeringAngle: Angle): AutonomousVehicle.this.type =
    modify(this)(_.gauges.steeringAngle)
      .setTo {
        MathUtils
          .withConstraint(steeringAngle,
                          -spec.maxSteeringAngle,
                          spec.maxSteeringAngle)
      }
      .asInstanceOf[this.type]

  override def withHeading(heading: Angle): AutonomousVehicle.this.type =
    modify(this)(_.gauges.heading)
      .setTo(heading)
      .modify(_.gauges.area)
      .setTo(Vehicle.calcArea(position, heading, spec))
      .asInstanceOf[this.type]

  override def withPosition(position: Point): AutonomousVehicle.this.type =
    modify(this)(_.gauges.position)
      .setTo(position)
      .modify(_.gauges.area)
      .setTo(Vehicle.calcArea(position, heading, spec))
      .asInstanceOf[this.type]

  override def withTargetVelocity(targetVelocity: Velocity): this.type =
    modify(this)(_.targetVelocity).setTo(targetVelocity).asInstanceOf[this.type]

  override def withAccelerationSchedule(
      accelerationSchedule: Option[AccelerationSchedule])
    : AutonomousVehicle.this.type = {
    modify(this)(_.accelerationSchedule)
      .setTo(accelerationSchedule)
      .asInstanceOf[this.type]
  }
}
