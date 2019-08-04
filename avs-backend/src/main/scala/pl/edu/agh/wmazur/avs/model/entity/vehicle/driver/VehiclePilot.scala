package pl.edu.agh.wmazur.avs.model.entity.vehicle.driver

import java.util.concurrent.TimeUnit

import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.road.Lane
import pl.edu.agh.wmazur.avs.model.entity.vehicle.BasicVehicle
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{
  Acceleration,
  Velocity
}
import pl.edu.agh.wmazur.avs.simulation.TickSource

import scala.concurrent.duration.FiniteDuration

trait VehiclePilot {
  self: VehicleDriver =>
  import VehiclePilot._

  protected def applyBasicThrothelling: VehiclePilot.this.type = {
    cruise.stopBeforeVehicleInFront().stopBeforeIntersection()
  }

  protected def stopBeforeIntersection(): self.type = {
    def stoppingDistance: Dimension = distanceTraveledIfStoppingNextFrame
    def minDistanceToIntersection: Dimension =
      stoppingDistance + stopDistanceBeforeIntersection

    applyIf(
      driverGauges.distanceToNextIntersection.exists(
        _.meters < minDistanceToIntersection.meters)) {
      withVehicle(vehicle.stop)
    }
  }

  protected def stopBeforeVehicleInFront(): VehiclePilot.this.type = {
    val breakingDistance =
      calcStoppingDistance(vehicle.velocity, vehicle.spec.maxDeceleration)
    val safeDistance = breakingDistance + minimumDistanceBetweenCars

    applyIf(driverGauges.distanceToCarInFront.exists(_ < safeDistance)) {
      withVehicle(vehicle.stop)
    }
  }

  protected def cruise: VehiclePilot.this.type =
    withVehicle {
      vehicle.maxAccelerationWithTargetVelocity {
        calcMaxAllowedVelocity(vehicle, currentLane)
      }
    }

  private def applyIf(predictate: Boolean)(
      newVehiclePilot: => self.type): self.type = {
    if (predictate) newVehiclePilot else this
  }

  private def velocityNextFrame: Velocity = {
    Math.min(
      calcMaxAllowedVelocity(vehicle, currentLane),
      vehicle.velocity + vehicle.spec.maxAcceleration * TickSource.timeStepSeconds
    )
  }

  private def distanceTraveledIfStoppingNextFrame: Dimension = {
    val distanceCovered = calcDistanceCoveredIfAccelerating(
      vehicle.velocity,
      vehicle.spec.maxAcceleration,
      calcMaxAllowedVelocity(vehicle, currentLane),
      TickSource.timeStep)

    val distanceToStop =
      calcStoppingDistance(velocityNextFrame, vehicle.spec.maxDeceleration)

    (distanceCovered + distanceToStop).fromMeters
  }

}

object VehiclePilot {
  val minimumDistanceBetweenCars: Dimension = 6.0.fromMeters
  val stopDistanceBeforeIntersection: Dimension = 1.0.fromMeters

  def calcStoppingDistance(velocity: Velocity,
                           deceleration: Acceleration): Dimension = {
    (-velocity * velocity / (2 * deceleration)).fromMeters
  }
  def calcMaxAllowedVelocity(vehicle: BasicVehicle, lane: Lane): Velocity =
    vehicle.spec.maxVelocity.min(lane.spec.speedLimit)

  def calcDistanceCoveredIfAccelerating(startVelocity: Velocity,
                                        targetVelocity: Velocity,
                                        acceleration: Acceleration,
                                        duration: FiniteDuration): Dimension = {
    val durationSeconds = duration.toUnit(TimeUnit.SECONDS)
    val maxChange = acceleration * durationSeconds
    val requestedChange = targetVelocity - startVelocity
    val accelerationDuration = (targetVelocity - startVelocity) / acceleration
    val avgAccelerationVelocity = (targetVelocity + startVelocity) / 2

    def calcIfAccelerating: Dimension = {
      if (startVelocity >= targetVelocity) {
        startVelocity * durationSeconds
      } else {

        if (requestedChange >= maxChange) {
          (startVelocity + (maxChange / 2)) * durationSeconds
        } else {
          avgAccelerationVelocity * acceleration + targetVelocity * (durationSeconds - accelerationDuration)
        }
      }
    }

    def calcIfDecelerating: Dimension = {
      if (startVelocity <= targetVelocity) {
        startVelocity * durationSeconds
      } else {
        if (requestedChange <= maxChange) {
          (startVelocity + (maxChange / 2)) * durationSeconds
        } else {
          avgAccelerationVelocity * accelerationDuration + targetVelocity * (durationSeconds - accelerationDuration)
        }
      }
    }

    val distance = if (acceleration >= 0) {
      calcIfAccelerating
    } else {
      calcIfDecelerating
    }
    distance.fromMeters
  }
}
