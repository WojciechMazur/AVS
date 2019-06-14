package pl.agh.edu.agh.wmazur.avs.model.entity.utils

trait DeltaOps[T] {
  def delta(old: T): Option[T]
}
