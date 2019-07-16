package pl.edu.agh.wmazur.avs.model.entity.utils

import pl.edu.agh.wmazur.avs.model.entity.Entity

trait IdProvider[T <: Entity] {
  private val iterator = Iterator.from(1)
  def nextId: Int = iterator.next()
}
