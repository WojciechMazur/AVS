package pl.edu.agh.wmazur.avs.model.entity.vehicle.movement

import pl.edu.agh.wmazur.avs.model.entity.utils.MathUtils._
import pl.edu.agh.wmazur.avs.model.entity.vehicle.Vehicle
import pl.edu.agh.wmazur.avs.model.entity.vehicle.movement.VehicleMovement.TimeDeltaSeconds

trait AcceleratingMovement extends VehicleMovement.VariableVehicleMovement {
  self: Vehicle
    with VehicleMovement
    with VehicleMovement.UniformVehicleMovement =>
  override def moveWithAcceleration(timeDelta: TimeDeltaSeconds): self.type =
    if (acceleration.isZero) {
      moveWithConstantVelocity(timeDelta)
    } else {
      val velocityDelta = acceleration * timeDelta
      val initialVelocity = velocity
      this
        .withAcceleration(initialVelocity + velocityDelta / 2)
        .move(timeDelta)
        .withAcceleration(initialVelocity + velocityDelta)
    }
}
