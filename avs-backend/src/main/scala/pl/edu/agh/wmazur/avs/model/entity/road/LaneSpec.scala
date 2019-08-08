package pl.edu.agh.wmazur.avs.model.entity.road

import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.Velocity

class LaneSpec(
    val speedLimit: Velocity,
    val width: Dimension,
    val canSpawn: Boolean = true,
    _leadsIntoLane: => Option[Lane] = None,
    _leadsFromLane: => Option[Lane] = None,
    _leftNeighbourLane: => Option[Lane] = None,
    _rightNeighbourLane: => Option[Lane] = None
) {
  lazy val leadsInto: Option[Lane] = _leadsIntoLane
  lazy val leadsFrom: Option[Lane] = _leadsFromLane
  lazy val leftNeighbourLane: Option[Lane] = _leftNeighbourLane
  lazy val rightNeighbourLane: Option[Lane] = _rightNeighbourLane

  def withSpawnEnabled(boolean: Boolean): LaneSpec = copy(canSpawn = boolean)

  var road: Option[Road] = None

  def copy(
      speedLimit: Velocity = this.speedLimit,
      width: Dimension = this.width,
      canSpawn: Boolean = this.canSpawn,
      leadsIntoLane: => Option[Lane] = this.leadsInto,
      leadsFromLane: => Option[Lane] = this.leadsFrom,
      leftNeighbourLane: => Option[Lane] = this.leftNeighbourLane,
      rightNeighbourLane: => Option[Lane] = this.rightNeighbourLane): LaneSpec =
    new LaneSpec(
      speedLimit = speedLimit,
      width = width,
      canSpawn = canSpawn,
      _leadsIntoLane = leadsIntoLane,
      _leadsFromLane = leadsFromLane,
      _leftNeighbourLane = leftNeighbourLane,
      _rightNeighbourLane = rightNeighbourLane
    )
  lazy val halfWidth: Dimension = width / 2.0
}
