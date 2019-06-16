package pl.edu.agh.wmazur.avs.model.entity.intersection

import pl.edu.agh.wmazur.avs.model.entity.utils.DeltaOps

trait Intersection extends DeltaOps[Intersection] {
  //TODO implementacja
  override def delta(old: Intersection): Option[Intersection] = None

  override def isUpdatedBy(old: Intersection): Boolean = false
}
