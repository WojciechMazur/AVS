package pl.edu.agh.wmazur.avs.model.entity.utils

import pl.edu.agh.wmazur.avs.model.entity.Entity

abstract class IdProvider[T <: Entity] {
  private val iterator = Iterator.from(1)
  def getId: Int = iterator.next()
}
