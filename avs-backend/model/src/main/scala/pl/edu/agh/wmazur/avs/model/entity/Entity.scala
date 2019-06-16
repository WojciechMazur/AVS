package pl.edu.agh.wmazur.avs.model.entity

import protobuf.pl.edu.agh.wmazur.avs.model.common.Vector3

trait Entity {
  type EntityId = String
  def id: EntityId
  def position: Vector3
}
