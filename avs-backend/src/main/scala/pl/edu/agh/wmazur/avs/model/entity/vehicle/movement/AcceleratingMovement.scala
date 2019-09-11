package pl.edu.agh.wmazur.avs.model.entity.vehicle.movement

import pl.edu.agh.wmazur.avs.model.entity.utils.MathUtils._
import pl.edu.agh.wmazur.avs.model.entity.vehicle.Vehicle
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.Acceleration
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
        .withVelocity(initialVelocity + velocityDelta / 2)
        .move(timeDelta)
        .withVelocity(initialVelocity + velocityDelta)
    }

  def setAndMoveWithAcceleration(acc: Acceleration,
                                 timeDelta: TimeDeltaSeconds): self.type = {
    withAcceleration(acc)
      .moveWithAcceleration(timeDelta)
  }
}
