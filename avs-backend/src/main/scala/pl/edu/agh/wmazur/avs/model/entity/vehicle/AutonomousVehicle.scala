package pl.edu.agh.wmazur.avs.model.entity.vehicle

import akka.actor.typed.ActorRef
import com.softwaremill.quicklens._
import org.locationtech.jts.geom.util.AffineTransformation
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

  override def withTargetVelocity(targetVelocity: Velocity): this.type = {

    def boundedTargetVelocity =
      MathUtils.withConstraint(targetVelocity,
                               spec.minVelocity,
                               spec.maxVelocity)
    this
      .modify(_.targetVelocity)
      .setTo(
        boundedTargetVelocity
//        acceleration match {
//          case a if a.isZero => boundedTargetVelocity
//          case a if a > 0.0  => spec.maxVelocity
//          case a if a < 0.0  => spec.minVelocity
//        }
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

  override def withVelocity(velocity: Velocity): this.type = {
    this
      .modify(_.gauges.velocity)
      .setTo(MathUtils
        .withConstraint(velocity, spec.minVelocity, spec.maxVelocity))
      .asInstanceOf[this.type]
  }

  override def withSteeringAngle(steeringAngle: Angle): this.type =
    this
      .modify(_.gauges.steeringAngle)
      .setTo(
        MathUtils.withConstraint(steeringAngle,
                                 -spec.maxSteeringAngle,
                                 spec.maxSteeringAngle))
      .asInstanceOf[this.type]

  override def withHeading(heading: Angle): this.type = {
    val rotation = heading - this.heading

    modify(this)(_.gauges.heading)
      .setTo(heading)
      .modify(_.gauges.geometry)
      .using(
        AffineTransformation
          .rotationInstance(rotation,
                            this.pointAtMiddleFront.getX,
                            this.pointAtMiddleFront.getY)
          .transform)
      .asInstanceOf[this.type]
  }

  override def withPosition(position: Point): this.type = {
    val xDelta = position.getX - this.position.getX
    val yDelta = position.getY - this.position.getY

    modify(this)(_.gauges.position)
      .setTo(position)
      .modify(_.gauges.geometry)
      .using(AffineTransformation.translationInstance(xDelta, yDelta).transform)
      .asInstanceOf[this.type]
  }

  override def withPositionAndHeading(position: Point,
                                      heading: Angle): this.type = {
    val rotation = heading - this.heading
    val xDelta = position.getX - this.position.getX
    val yDelta = position.getY - this.position.getY

    modify(this)(_.gauges.heading)
      .setTo(heading)
      .modify(_.gauges.position)
      .setTo(position)
      .modify(_.gauges.geometry)
      .using(
        AffineTransformation
          .translationInstance(xDelta, yDelta)
          .compose(AffineTransformation.rotationInstance(rotation))
          .transform)
      .asInstanceOf[this.type]
  }

  override def withAccelerationSchedule(
      accelerationSchedule: Option[AccelerationSchedule]): this.type = {
    modify(this)(_.accelerationSchedule)
      .setTo(accelerationSchedule)
      .asInstanceOf[this.type]
  }
}
