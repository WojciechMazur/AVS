package pl.edu.agh.wmazur.avs.model.entity.vehicle
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
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.{
  EmptyDriver,
  VehicleDriver
}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.movement.VelocityReachingMovement

case class BasicVehicle(
    id: Vin,
    targetVelocity: Velocity,
    gauges: VehicleGauges,
    spec: VehicleSpec,
    driver: VehicleDriver,
    spawnTime: Int
) extends Vehicle
    with VelocityReachingMovement {

  override def withTargetVelocity(targetVelocity: Velocity): BasicVehicle =
    copy(targetVelocity = targetVelocity)
  override def withAcceleration(acceleration: Acceleration): BasicVehicle =
    this.modify(_.gauges.acceleration).setTo(acceleration)
  override def withVelocity(velocity: Velocity): BasicVehicle =
    this.modify(_.gauges.velocity).setTo(velocity)
  override def withSteeringAngle(steeringAngle: Angle): BasicVehicle =
    this
      .modify(_.gauges.steeringAngle)
      .setTo(
        MathUtils.withConstraint(steeringAngle,
                                 -spec.maxSteeringAngle,
                                 spec.maxSteeringAngle))
  override def withHeading(heading: Angle): BasicVehicle =
    this.modify(_.gauges.heading).setTo(heading)
  override def withPosition(position: Point): BasicVehicle =
    this.modify(_.gauges.position).setTo(position)

  override def withDriver(driver: VehicleDriver): Vehicle =
    this.modify(_.driver).setTo(driver)

  override lazy val area: Shape = {
    PolygonFactory(
      cornerPoints,
      position
    )
  }

  override type Self = BasicVehicle
  override def entitySettings: EntitySettings[BasicVehicle] = BasicVehicle
}

object BasicVehicle extends EntitySettings[BasicVehicle] {
  //scalastyle:off
  def apply(
      position: Point,
      optDriver: => Option[VehicleDriver] = None,
      heading: Angle = 0.0,
      velocity: Velocity = 0.0,
      targetVelocity: Option[Velocity] = None,
      acceleration: Acceleration = 0.0,
      steeringAngle: Angle = 0.0,
      spec: VehicleSpec = VehicleSpec.Predefined.Sedan,
      spawnTime: Int = 0
  ): BasicVehicle = {
    val gauges = VehicleGauges(position,
                               velocity = velocity,
                               acceleration = acceleration,
                               steeringAngle = steeringAngle,
                               heading = heading)

    lazy val driver: VehicleDriver =
      optDriver.getOrElse(new EmptyDriver(vehicle))
    lazy val vehicle: BasicVehicle = new BasicVehicle(
      id = Vehicle.nextId,
      targetVelocity = targetVelocity.getOrElse(spec.maxVelocity),
      gauges = gauges,
      spec = spec,
      driver = driver,
      spawnTime = spawnTime
    )
    vehicle
  }
  //scalastyle:on
}
