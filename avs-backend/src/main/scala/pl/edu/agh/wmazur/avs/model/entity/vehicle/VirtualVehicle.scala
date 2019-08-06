package pl.edu.agh.wmazur.avs.model.entity.vehicle

import com.softwaremill.quicklens._
import org.locationtech.spatial4j.shape.Point
import pl.edu.agh.wmazur.avs.model.entity.utils.MathUtils
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{
  Acceleration,
  Angle,
  Velocity
}

case class VirtualVehicle(
    var gauges: VehicleGauges,
    spec: VehicleSpec,
    var targetVelocity: Velocity,
    spawnTime: Int
) extends BasicVehicle(-1L, gauges, spec, targetVelocity, spawnTime) {

  override def id: Id = -1L

  override def withAcceleration(
      acceleration: Acceleration): VirtualVehicle.this.type = {
    modify(this)(_.gauges.acceleration)
      .setTo(acceleration)
      .asInstanceOf[this.type]
  }
  override def withVelocity(velocity: Velocity): VirtualVehicle.this.type =
    modify(this)(_.gauges.velocity)
      .setTo(velocity)
      .asInstanceOf[this.type]

  override def withSteeringAngle(
      steeringAngle: Angle): VirtualVehicle.this.type =
    modify(this)(_.gauges.steeringAngle)
      .setTo {
        MathUtils
          .withConstraint(steeringAngle,
                          -spec.maxSteeringAngle,
                          spec.maxSteeringAngle)
      }
      .asInstanceOf[this.type]

  override def withHeading(heading: Angle): VirtualVehicle.this.type =
    modify(this)(_.gauges.heading)
      .setTo(heading)
      .modify(_.gauges.area)
      .setTo(Vehicle.calcArea(position, heading, spec))
      .asInstanceOf[this.type]

  override def withPosition(position: Point): VirtualVehicle.this.type =
    modify(this)(_.gauges.position)
      .setTo(position)
      .modify(_.gauges.area)
      .setTo(Vehicle.calcArea(position, heading, spec))
      .asInstanceOf[this.type]

  override def withTargetVelocity(targetVelocity: Velocity): this.type =
    modify(this)(_.targetVelocity).setTo(targetVelocity).asInstanceOf[this.type]
}
