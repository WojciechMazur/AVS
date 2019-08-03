package pl.edu.agh.wmazur.avs.model.entity.vehicle

import org.locationtech.spatial4j.shape.{Point, Shape}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{
  Acceleration,
  Angle,
  Velocity
}

case class VehicleGauges(
    position: Point,
    velocity: Velocity,
    acceleration: Acceleration,
    steeringAngle: Angle,
    heading: Angle, //in radians
    area: Shape,
)
