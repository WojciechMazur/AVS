package pl.edu.agh.wmazur.avs.model.entity.vehicle

import com.github.jpbetz.subspace.Vector3

case class DefaultVehicle(id: String,
                          position: Vector3 = Vector3.fill(0),
                          speed: Float = 0,
                          acceleration: Float = 0)
    extends Vehicle
