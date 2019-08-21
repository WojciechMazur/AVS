package pl.edu.agh.wmazur.avs.model.entity.road

import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.road.TurningAllowance.AnyDirection
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.Velocity

class LaneSpec(
    val speedLimit: Velocity,
    val width: Dimension,
    val canSpawn: Boolean = true,
    val turningAllowance: TurningAllowance = AnyDirection,
    var leadsInto: Option[Lane] = None,
    var leadsFrom: Option[Lane] = None,
    var leftNeighbour: Option[Lane] = None,
    var rightNeighbour: Option[Lane] = None
) {

  def withSpawnEnabled(boolean: Boolean): LaneSpec = copy(canSpawn = boolean)
  def withTurningAllowance(newAllowance: TurningAllowance): LaneSpec =
    copy(turningAllowance = newAllowance)
  def leadsInto(lane: Lane): LaneSpec = {
    this.leadsInto = Some(lane)
    this
  }
  def leadsFrom(lane: Lane): LaneSpec = {
    this.leadsFrom = Some(lane)
    this
  }

  var road: Option[Road] = None

  def copy(
      speedLimit: Velocity = this.speedLimit,
      width: Dimension = this.width,
      canSpawn: Boolean = this.canSpawn,
      turningAllowance: TurningAllowance = this.turningAllowance,
      leadsIntoLane: => Option[Lane] = this.leadsInto,
      leadsFromLane: => Option[Lane] = this.leadsFrom,
      leftNeighbourLane: => Option[Lane] = this.leftNeighbour,
      rightNeighbourLane: => Option[Lane] = this.rightNeighbour): LaneSpec =
    new LaneSpec(
      speedLimit = speedLimit,
      width = width,
      canSpawn = canSpawn,
      turningAllowance = turningAllowance,
      leadsInto = leadsIntoLane,
      leadsFrom = leadsFromLane,
      leftNeighbour = leftNeighbourLane,
      rightNeighbour = rightNeighbourLane
    )
  lazy val halfWidth: Dimension = width / 2.0
}
