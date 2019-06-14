package pl.agh.edu.agh.wmazur.avs.model.entity.vehicle

import pl.agh.edu.agh.wmazur.avs.model.entity.Entity
import pl.agh.edu.agh.wmazur.avs.model.entity.utils.DeltaOps

trait Vehicle extends Entity with DeltaOps[Vehicle] {
  override def delta(old: Vehicle): Option[Vehicle] = None
  def speed: Float
  def acceleration: Float

}
