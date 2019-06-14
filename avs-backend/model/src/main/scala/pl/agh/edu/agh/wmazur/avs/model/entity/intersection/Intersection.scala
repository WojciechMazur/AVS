package pl.agh.edu.agh.wmazur.avs.model.entity.intersection

import pl.agh.edu.agh.wmazur.avs.model.entity.utils.DeltaOps

trait Intersection extends DeltaOps[Intersection] {
  //TODO implementacja
  override def delta(old: Intersection): Option[Intersection] = None
}
