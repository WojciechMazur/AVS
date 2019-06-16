package pl.edu.agh.wmazur.avs.model.entity.vehicle

import protobuf.pl.edu.agh.wmazur.avs.model.common.Vector3

case class DefaultVehicle(id: String,
                          position: Vector3 = Vector3.defaultInstance,
                          speed: Float = 0,
                          acceleration: Float = 0)
    extends Vehicle
