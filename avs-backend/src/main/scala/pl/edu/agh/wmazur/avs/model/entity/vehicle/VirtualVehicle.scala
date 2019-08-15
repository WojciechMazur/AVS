package pl.edu.agh.wmazur.avs.model.entity.vehicle

import com.softwaremill.quicklens._
import org.locationtech.spatial4j.shape.Point
import pl.edu.agh.wmazur.avs.model.entity.utils.MathUtils
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{
  Acceleration,
  Angle,
  Velocity
}
import pl.edu.agh.wmazur.avs.model.entity.utils.MathUtils.DoubleUtils
case class VirtualVehicle(
    var gauges: VehicleGauges,
    spec: VehicleSpec,
    var targetVelocity: Velocity,
    spawnTime: Int
) extends BasicVehicle(-1L, gauges, spec, targetVelocity, spawnTime) {

  override def id: Id = -1L

  override def withTargetVelocity(targetVelocity: Velocity): this.type = {
    def boundedTargetVelocity =
      MathUtils.withConstraint(targetVelocity,
                               spec.minVelocity,
                               spec.maxVelocity)

    this
      .modify(_.targetVelocity)
      .setTo(
        acceleration match {
          case a if a.isZero => boundedTargetVelocity
          case a if a > 0.0  => spec.maxVelocity
          case a if a < 0.0  => spec.minVelocity
        }
      )
      .asInstanceOf[this.type]
  }

  override def withAcceleration(acceleration: Acceleration): this.type =
    this
      .modify(_.gauges.acceleration)
      .setTo(
        MathUtils.withConstraint(acceleration,
                                 spec.maxDeceleration,
                                 spec.maxAcceleration))
      .asInstanceOf[this.type]

  override def withVelocity(velocity: Velocity): this.type =
    this
      .modify(_.gauges.velocity)
      .setTo(MathUtils
        .withConstraint(velocity, spec.minVelocity, spec.maxVelocity))
      .asInstanceOf[this.type]

  override def withSteeringAngle(steeringAngle: Angle): this.type =
    this
      .modify(_.gauges.steeringAngle)
      .setTo(
        MathUtils.withConstraint(steeringAngle,
                                 -spec.maxSteeringAngle,
                                 spec.maxSteeringAngle))
      .asInstanceOf[this.type]

  override def withHeading(heading: Angle): this.type =
    modify(this)(_.gauges.heading)
      .setTo(heading)
      .modify(_.gauges.area)
      .setTo(Vehicle.calcArea(position, heading, spec))
      .asInstanceOf[this.type]

  override def withPosition(position: Point): this.type =
    modify(this)(_.gauges.position)
      .setTo(position)
      .modify(_.gauges.area)
      .setTo(Vehicle.calcArea(position, heading, spec))
      .asInstanceOf[this.type]

  override def accelerationSchedule: Option[AccelerationSchedule] = None

  override def withAccelerationSchedule(
      accelerationSchedule: Option[AccelerationSchedule])
    : VirtualVehicle.this.type = this
}
