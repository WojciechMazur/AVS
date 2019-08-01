package pl.edu.agh.wmazur.avs.model.entity.vehicle

import org.locationtech.spatial4j.shape.Point
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{
  Acceleration,
  Angle,
  Velocity
}

case class VehicleGauges(
    var position: Point,
    var velocity: Velocity,
    var acceleration: Acceleration,
    var steeringAngle: Angle,
    var heading: Angle //in radians
)
