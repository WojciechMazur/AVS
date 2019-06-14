package pl.agh.edu.agh.wmazur.avs.model.entity

import protobuf.pl.agh.edu.agh.wmazur.avs.model.common.Position

trait Entity {
  type EntityId = String
  def id: EntityId
  def position: Position
}
