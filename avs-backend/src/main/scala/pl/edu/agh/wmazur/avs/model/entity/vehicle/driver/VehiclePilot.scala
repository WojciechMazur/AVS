package pl.edu.agh.wmazur.avs.model.entity.vehicle.driver

import java.util.concurrent.TimeUnit

import com.softwaremill.quicklens._
import org.locationtech.spatial4j.shape.Point
import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.intersection.reservation.ReservationArray.Timestamp
import pl.edu.agh.wmazur.avs.model.entity.road.Lane
import pl.edu.agh.wmazur.avs.model.entity.vehicle.AccelerationProfile.AccelerationEvent
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{
  Acceleration,
  Velocity
}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.VehicleArrivalEstimator.Parameters
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.VehicleDriver.Protocol.ReservationDetails
import pl.edu.agh.wmazur.avs.model.entity.vehicle.{
  AccelerationSchedule,
  BasicVehicle
}
import pl.edu.agh.wmazur.avs.simulation.TickSource

import scala.annotation.tailrec
import scala.concurrent.duration.{FiniteDuration, _}

trait VehiclePilot {
  self: VehicleDriver =>
  import VehiclePilot._

  final val defaultLeadTime = 0.1.seconds
  final val traversingLaneChangeLeadTime = 0.1.seconds
  final val minimalLeadDistance = 0.2.meters

  @tailrec
  final protected def followCurrentLane(
      leadTime: FiniteDuration = defaultLeadTime): self.type = {
    val leadDistance = leadTime.toUnit(TimeUnit.SECONDS) * vehicle.velocity + minimalLeadDistance.asMeters
    val remainingDistance =
      currentLane.remainingDistanceAlongLane(vehicle.position)

    if (leadDistance > remainingDistance.asMeters) {
      currentLane.spec.leadsInto match {
        case Some(nextLane) if remainingDistance <= 0.0 =>
          setCurrentLane(nextLane)
          followCurrentLane(leadTime)

        case Some(nextLane) =>
          turnTowardPoint {
            nextLane.leadPointOf(
              point = nextLane.entryPoint,
              distance = leadDistance.asMeters - remainingDistance)
          }
      }
    } else {
      turnTowardPoint {
        currentLane.leadPointOf(vehicle.position, leadDistance)
      }
    }
  }

  protected def takeSteeringActiorsForTraversing(
      details: ReservationDetails): self.type = {
    def changesLane = currentLane.id != details.departureLaneId
    def changesRoad = currentLane.spec.road.get.id != details.departureRoadId
    if (changesLane && changesRoad) {
      val distanceToLane = details.departureLane.geometry
        .distance(vehicle.geometry)
        .geoDegrees

      val laneChangeDistance = (traversingLaneChangeLeadTime.toUnit(
        TimeUnit.SECONDS) * vehicle.velocity).meters

      if (distanceToLane < laneChangeDistance) {
        setCurrentLane(details.departureLane)
      }
    }
    followCurrentLane()
  }

  protected def followAccelerationProfile(details: ReservationDetails,
                                          timeDelta: FiniteDuration,
                                          maxCrossingVelocity: Velocity)
    : (ReservationDetails, VehiclePilot.this.type) = {

    @tailrec
    def iterate(events: List[AccelerationEvent],
                totalAcceleration: Acceleration,
                remainingTime: FiniteDuration)
      : (Acceleration, List[AccelerationEvent]) = {
      val remainingSeconds = remainingTime.toUnit(TimeUnit.SECONDS)
      events match {
        case current :: remaining if current.duration > remainingTime =>
          val finalAcceleration = remainingSeconds * current.acceleration
          val updatedEvent = current.modify(_.duration).using(_ - remainingTime)
          (finalAcceleration, updatedEvent :: remaining)
        case current :: remaining if current.duration < remainingTime =>
          val accAcceleration = totalAcceleration + current.duration.toUnit(
            TimeUnit.SECONDS) * current.acceleration
          val updatedRemainingTime = remainingTime - current.duration
          iterate(remaining, accAcceleration, updatedRemainingTime)
        case current :: remaining =>
          val finalAcceleration = totalAcceleration + current.acceleration * current.duration
            .toUnit(TimeUnit.SECONDS)
          (finalAcceleration, remaining)
        case Nil if remainingTime > Duration.Zero =>
          val finalAcceleration = totalAcceleration + remainingSeconds * events.headOption
            .map(_.acceleration)
            .getOrElse(0.0)
          (finalAcceleration, Nil)
        case Nil => (totalAcceleration, Nil)
      }

    }
    val events = details.accelerationProfile.events
    val (updatedVehicle, remainingEvents) = events match {
      case Nil =>
        (vehicle.maxAccelerationWithTargetVelocity(maxCrossingVelocity), Nil)
      case current :: remaining if current.duration > timeDelta =>
        val updatedVehicle =
          vehicle.maxVelocityWithAcceleration(current.acceleration)
        val updatedEvent = current.modify(_.duration).using(_ - timeDelta)
        (updatedVehicle, updatedEvent :: remaining)
      case current :: _ if current.duration < timeDelta =>
        val (acceleration, remainingEvents) = iterate(events = events,
                                                      totalAcceleration = 0.0,
                                                      remainingTime = timeDelta)
        val updatedVehicle = vehicle.maxVelocityWithAcceleration(
          acceleration = acceleration / timeDelta.toUnit(TimeUnit.SECONDS)
        )
        (updatedVehicle, remainingEvents)

      case current :: remaining =>
        (vehicle.maxVelocityWithAcceleration(current.acceleration), remaining)
    }

    val updatedDetails =
      details
        .modify(_.accelerationProfile.events)
        .setTo(remainingEvents)
    (updatedDetails, withVehicle(updatedVehicle))
  }

  protected def hasClearLaneToIntersection: Boolean = {
    (driverGauges.distanceToNextIntersection, driverGauges.distanceToCarInFront) match {
      case (Some(distI), Some(distV)) if distV < distI =>
        val stoppingDistance = calcStoppingDistance(
          vehicle.velocity,
          vehicle.spec.maxDeceleration) + minimumDistanceBetweenCars
        driverGauges.distanceToCollisionWithCarInFront.forall(
          _ > stoppingDistance)

      case _ => true
    }
  }

  protected def followNewLane(): self.type = {
    val leadDistance = minimalLeadDistance * vehicle.velocity + minimalLeadDistance
    turnTowardPoint {
      currentLane.leadPointOf(vehicle.position, leadDistance)
    }
  }

  protected def turnTowardPoint(point: Point): self.type = {
    withVehicle {
      vehicle.moveWheelsTowardPoint(point)
    }
  }

  protected def applyBasicThrothelling(): VehiclePilot.this.type = {
    cruise.stopBeforeVehicleInFront().stopBeforeIntersection()
  }

  protected def stopBeforeIntersection(): self.type = {
    def stoppingDistance: Dimension = distanceTraveledIfStoppingNextFrame
    def minDistanceToIntersection: Dimension =
      stoppingDistance + stopDistanceBeforeIntersection

    applyIf(
      driverGauges.distanceToNextIntersection.exists(
        _.asMeters < minDistanceToIntersection.asMeters)) {
      withVehicle(vehicle.stop)
    }
  }

  protected def stopBeforeIntersectionSchedule(
      currentTime: Timestamp): Option[AccelerationSchedule] = {
    for {
      distanceToIntersection <- driverGauges.distanceToNextIntersection
        .filter(_ > VehiclePilot.stopDistanceBeforeIntersection)
      maxVelocity = calcMaxAllowedVelocity(vehicle, currentLane)
//      _ = print("Estimating to stop before intersection ::")
      estimation <- VehicleArrivalEstimator
        .estimate(
          Parameters(
            initialTime = currentTime,
            velocity = vehicle.velocity,
            distanceTotal = distanceToIntersection,
            maxVelocity = maxVelocity,
            finalVelocity = 0,
            maxAcceleration = vehicle.spec.maxAcceleration,
            maxDeceleration = vehicle.spec.maxDeceleration,
          ))
        .toOption
    } yield estimation.accelerationSchedule
  }

  protected def stopBeforeVehicleInFront(): VehiclePilot.this.type = {
    val breakingDistance = distanceTraveledIfStoppingNextFrame
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

    (distanceCovered + distanceToStop).meters
  }

}

object VehiclePilot {
  val minimumDistanceBetweenCars: Dimension = 6.0.meters
  val stopDistanceBeforeIntersection: Dimension = 1.9.meters

  def calcStoppingDistance(velocity: Velocity,
                           deceleration: Acceleration): Dimension = {
    (-velocity * velocity / (2 * deceleration)).meters
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
    distance.meters
  }
}
