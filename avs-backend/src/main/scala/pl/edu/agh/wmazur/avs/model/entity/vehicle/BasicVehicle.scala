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

abstract class BasicVehicle(
    id: Vin,
    gauges: VehicleGauges,
    spec: VehicleSpec,
    targetVelocity: Velocity,
    spawnTime: Int
) extends Vehicle
    with VelocityReachingMovement {

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

    lazy val vehicle: BasicVehicle = AutonomousVehicle(
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
