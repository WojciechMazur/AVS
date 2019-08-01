package pl.edu.agh.wmazur.avs.model.entity.utils

trait DeltaOps[T] {
  def isUpdatedBy(old: T): Boolean
}
