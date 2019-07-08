package pl.edu.agh.wmazur.avs.model.entity

import pl.edu.agh.wmazur.avs.model.entity.utils.IdProvider

trait EntitySettings[T <: Entity] {
  def idProvider: IdProvider[T] = new IdProvider[T] {}
}
