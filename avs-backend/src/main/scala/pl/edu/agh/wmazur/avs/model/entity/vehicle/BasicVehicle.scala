package pl.edu.agh.wmazur.avs.model.entity.vehicle
import akka.actor.typed.ActorRef
import org.locationtech.spatial4j.shape.Point
import pl.edu.agh.wmazur.avs.model.entity.EntitySettings
import pl.edu.agh.wmazur.avs.model.entity.vehicle.Vehicle.Vin
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{
  Acceleration,
  Angle,
  Velocity
}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousVehicleDriver
import pl.edu.agh.wmazur.avs.model.entity.vehicle.movement.{
  AccelerationScheduleMovement,
  VelocityReachingMovement
}

abstract class BasicVehicle(
    id: Vin,
    gauges: VehicleGauges,
    spec: VehicleSpec,
    targetVelocity: Velocity,
    spawnTime: Int
) extends Vehicle
    with VelocityReachingMovement
    with AccelerationScheduleMovement {

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
      geometry = Vehicle.calcGeometry(position, heading, spec)
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
