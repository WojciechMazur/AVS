package pl.edu.agh.wmazur.avs.model.entity.road

import pl.edu.agh.wmazur.avs.model.entity.utils.DeltaOps

trait Road extends DeltaOps[Road] {
  //TODO implementacja
  override def delta(old: Road): Option[Road] = None

  override def isUpdatedBy(old: Road): Boolean = false
}
