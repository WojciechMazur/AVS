package pl.edu.agh.wmazur.avs.model.entity.road

import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.Velocity

class LaneSpec(
    val speedLimit: Velocity,
    val width: Dimension,
    _leadsIntoLane: => Option[Lane] = None,
    _leadsFromLane: => Option[Lane] = None,
    _leftNeighbourLane: => Option[Lane] = None,
    _rightNeighbourLane: => Option[Lane] = None
) {
  lazy val leadsIntoLane: Option[Lane] = _leadsIntoLane
  lazy val leadsFromLane: Option[Lane] = _leadsFromLane
  lazy val leftNeighbourLane: Option[Lane] = _leftNeighbourLane
  lazy val rightNeighbourLane: Option[Lane] = _rightNeighbourLane

  var road: Option[Road] = None

  def copy(
      speedLimit: Velocity = this.speedLimit,
      width: Dimension = this.width,
      leadsIntoLane: => Option[Lane] = this.leadsIntoLane,
      leadsFromLane: => Option[Lane] = this.leadsFromLane,
      leftNeighbourLane: => Option[Lane] = this.leftNeighbourLane,
      rightNeighbourLane: => Option[Lane] = this.rightNeighbourLane): LaneSpec =
    new LaneSpec(
      speedLimit,
      width,
      leadsIntoLane,
      leadsFromLane,
      leftNeighbourLane,
      rightNeighbourLane
    )
  lazy val halfWidth: Dimension = width / 2.0
}
