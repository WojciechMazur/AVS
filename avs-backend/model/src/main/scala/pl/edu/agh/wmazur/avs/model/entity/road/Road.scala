package pl.edu.agh.wmazur.avs.model.entity.road

import pl.edu.agh.wmazur.avs.model.entity.Entity
import pl.edu.agh.wmazur.avs.model.entity.utils.DeltaOps

trait Road extends Entity with DeltaOps[Road] {
  def lanes: List[Lane]
  def oppositeRoad: Option[Road]

  override def isUpdatedBy(old: Road): Boolean = false
}
