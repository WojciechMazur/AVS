package pl.agh.edu.agh.wmazur.avs.model.entity.vehicle

import protobuf.pl.agh.edu.agh.wmazur.avs.model.common.Position

case class DefaultVehicle(id: String,
                          position: Position = Position.defaultInstance,
                          speed: Float = 0,
                          acceleration: Float = 0)
    extends Vehicle
