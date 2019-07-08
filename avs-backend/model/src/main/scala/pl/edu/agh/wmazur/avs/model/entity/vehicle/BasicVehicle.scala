package pl.edu.agh.wmazur.avs.model.entity.vehicle
import org.locationtech.spatial4j.shape.{Point, Shape}
import pl.edu.agh.wmazur.avs.model.entity.{Entity, EntitySettings}
import pl.edu.agh.wmazur.avs.model.entity.utils.MathUtils
import pl.edu.agh.wmazur.avs.model.entity.vehicle.Vehicle.Vin
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{
  Acceleration,
  Angle,
  Velocity
}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.movement.VelocityReachingMovement

case class BasicVehicle(
    id: Vin,
    position: Point,
    heading: Angle,
    velocity: Velocity,
    targetVelocity: Velocity,
    override val acceleration: Acceleration,
    override val steeringAngle: Angle,
    spec: VehicleSpec,
    spawnTime: Int
) extends Vehicle
    with VelocityReachingMovement {

  override def withTargetVelocity(targetVelocity: Velocity): BasicVehicle =
    copy(targetVelocity = targetVelocity)
  override def withAcceleration(acceleration: Acceleration): BasicVehicle =
    copy(acceleration = acceleration)
  override def withVelocity(velocity: Velocity): BasicVehicle =
    copy(velocity = velocity)
  override def withSteeringAngle(steeringAngle: Angle): BasicVehicle =
    copy(
      steeringAngle = MathUtils.withConstraint(steeringAngle,
                                               -spec.maxSteeringAngle,
                                               spec.maxSteeringAngle))
  override def withHeading(heading: Angle): BasicVehicle =
    copy(heading = heading)
  override def withPosition(position: Point): BasicVehicle =
    copy(position = position)

  override lazy val area: Shape = ???

  override type Self = BasicVehicle
  override val entitySettings: EntitySettings[BasicVehicle] = BasicVehicle
}

object BasicVehicle extends EntitySettings[BasicVehicle]
