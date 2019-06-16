package pl.edu.agh.wmazur.avs.model.entity.vehicle

import pl.edu.agh.wmazur.avs.model.entity.Entity
import pl.edu.agh.wmazur.avs.model.entity.utils.DeltaOps

trait Vehicle extends Entity with DeltaOps[Vehicle] {
  override def delta(old: Vehicle): Option[Vehicle] = None
  def speed: Float
  def acceleration: Float

  override def isUpdatedBy(old: Vehicle): Boolean = {
    this.position != old.position ||
    Math.abs(this.acceleration - old.acceleration) > 0.001 ||
    Math.abs(this.speed - old.speed) > 0.001
  }
}
