package pl.edu.agh.wmazur.avs.model.entity.road

import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.road.TurningAllowance.AnyDirection
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.Velocity

case class LaneSpec(
    speedLimit: Velocity,
    width: Dimension,
    canSpawn: Boolean = true,
    turningAllowance: TurningAllowance = AnyDirection,
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

  override def toString: String =
    s"LaneSpec(limit: $speedLimit, width: $width, canSpawn: ${canSpawn}, turningAllowance: $turningAllowance)"

  var road: Option[Road] = None
  lazy val halfWidth: Dimension = width / 2.0
}
