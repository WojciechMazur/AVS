package pl.edu.agh.wmazur.avs.model.entity.road

import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{
  Dimension,
  Velocity
}

case class LaneSpec(
    speedLimit: Velocity,
    width: Dimension,
    road: Road,
    leadsIntoLane: Option[Lane] = None,
    leadsFromLane: Option[Lane] = None,
    leftNeighbourLane: Option[Lane] = None,
    rightNeighbourLane: Option[Lane] = None
) {
  lazy val halfWidth: Dimension = width / 2
}
