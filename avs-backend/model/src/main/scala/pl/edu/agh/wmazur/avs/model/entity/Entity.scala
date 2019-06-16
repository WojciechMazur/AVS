package pl.edu.agh.wmazur.avs.model.entity

import com.github.jpbetz.subspace.Vector3

trait Entity {
  type EntityId = String
  def id: EntityId
  def position: Vector3
}
