package pl.edu.agh.wmazur.avs.model.entity.road

import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{
  Dimension,
  Velocity
}

case class LaneSpec(
    speedLimit: Velocity,
    width: Dimension,
    length: Dimension,
) {
  lazy val halfWidth: Dimension = width / 2
}
