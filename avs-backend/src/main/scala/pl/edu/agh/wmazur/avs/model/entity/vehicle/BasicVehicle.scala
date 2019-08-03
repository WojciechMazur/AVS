package pl.edu.agh.wmazur.avs.model.entity.vehicle
import akka.actor.typed.ActorRef
import com.softwaremill.quicklens._
import org.locationtech.spatial4j.shape.{Point, Shape}
import pl.edu.agh.wmazur.avs.model.entity.EntitySettings
import pl.edu.agh.wmazur.avs.model.entity.utils.MathUtils
import pl.edu.agh.wmazur.avs.model.entity.utils.SpatialUtils.PolygonFactory
import pl.edu.agh.wmazur.avs.model.entity.vehicle.Vehicle.Vin
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{
  Acceleration,
  Angle,
  Velocity
}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousVehicleDriver
import pl.edu.agh.wmazur.avs.model.entity.vehicle.movement.VelocityReachingMovement

case class BasicVehicle(
    id: Vin,
    gauges: VehicleGauges,
    spec: VehicleSpec,
    targetVelocity: Velocity,
    driverRef: ActorRef[AutonomousVehicleDriver.ExtendedProtocol],
    spawnTime: Int
) extends Vehicle
    with VelocityReachingMovement {

  override def withAcceleration(
      acceleration: Acceleration): BasicVehicle.this.type = {
    modify(this)(_.gauges.acceleration)
      .setTo(acceleration)
      .asInstanceOf[this.type]
  }
  override def withVelocity(velocity: Velocity): BasicVehicle.this.type =
    modify(this)(_.gauges.velocity)
      .setTo(velocity)
      .asInstanceOf[this.type]

  override def withSteeringAngle(steeringAngle: Angle): BasicVehicle.this.type =
    modify(this)(_.gauges.steeringAngle)
      .setTo {
        MathUtils
          .withConstraint(steeringAngle,
                          -spec.maxSteeringAngle,
                          spec.maxSteeringAngle)
      }
      .asInstanceOf[this.type]

  override def withHeading(heading: Angle): BasicVehicle.this.type =
    modify(this)(_.gauges.heading)
      .setTo(heading)
      .modify(_.gauges.area)
      .setTo(Vehicle.calcArea(position, heading, spec))
      .asInstanceOf[this.type]

  override def withPosition(position: Point): BasicVehicle.this.type =
    modify(this)(_.gauges.position)
      .setTo(position)
      .modify(_.gauges.area)
      .setTo(Vehicle.calcArea(position, heading, spec))
      .asInstanceOf[this.type]

  override def withTargetVelocity(targetVelocity: Velocity): this.type =
    modify(this)(_.targetVelocity).setTo(targetVelocity).asInstanceOf[this.type]

  override type Self = BasicVehicle
  override def entitySettings: EntitySettings[BasicVehicle] = BasicVehicle
}

object BasicVehicle extends EntitySettings[BasicVehicle] {
  //scalastyle:off
  def apply(
      id: Option[Vehicle#Id] = None,
      position: Point,
      driverRef: ActorRef[AutonomousVehicleDriver.ExtendedProtocol],
      heading: Angle = 0.0,
      velocity: Velocity = 0.0,
      targetVelocity: Option[Velocity] = None,
      acceleration: Acceleration = 0.0,
      steeringAngle: Angle = 0.0,
      spec: VehicleSpec = VehicleSpec.Predefined.Sedan,
      spawnTime: Int = 0
  ): BasicVehicle = {
    val gauges = VehicleGauges(
      position,
      velocity = velocity,
      acceleration = acceleration,
      steeringAngle = steeringAngle,
      heading = heading,
      area = Vehicle.calcArea(position, heading, spec)
    )

    lazy val vehicle: BasicVehicle = new BasicVehicle(
      id = id.getOrElse(Vehicle.nextId),
      targetVelocity = targetVelocity.getOrElse(spec.maxVelocity),
      gauges = gauges,
      spec = spec,
      driverRef = driverRef,
      spawnTime = spawnTime
    )

    vehicle
  }
  //scalastyle:on
}