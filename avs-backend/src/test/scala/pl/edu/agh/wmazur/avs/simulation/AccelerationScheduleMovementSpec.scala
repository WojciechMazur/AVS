package pl.edu.agh.wmazur.avs.simulation

import java.util.concurrent.TimeUnit

import org.scalatest.{FlatSpec, WordSpec}
import pl.edu.agh.wmazur.avs.model.entity.road.{DirectedLane, LaneSpec}
import pl.edu.agh.wmazur.avs.model.entity.utils.SpatialUtils.Point2
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.{
  CrashTestDriver,
  VehicleDriver
}
import com.softwaremill.quicklens._
import pl.edu.agh.wmazur.avs.model.entity.vehicle.{
  AccelerationProfile,
  BasicVehicle,
  Vehicle,
  VehicleGauges,
  VehicleSpec,
  VirtualVehicle
}
import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.intersection.reservation.ReservationArray.Timestamp

import scala.annotation.tailrec
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}
import pl.edu.agh.wmazur.avs.model.entity.utils.SpatialUtils._
import pl.edu.agh.wmazur.avs.model.entity.vehicle.AccelerationProfile.AccelerationEvent
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.Velocity

class AccelerationScheduleMovementSpec extends WordSpec {
  case class VehicleState(vehicle: BasicVehicle,
                          distanceCovered: Dimension,
                          duration: FiniteDuration,
                          currentTime: Timestamp)

  val initialPosition = Point2(0.0, 0.0)
  val endPosition = Point2(1000.0.meters.asGeoDegrees, 0.0)
  val initialHeading = 0.0
  val initialVelocity = 0.0
  val expectedDistance: Dimension = initialPosition.distance(endPosition)
  def distancePrecision(velocity: Velocity): Dimension =
    (TickSource.timeStepSeconds * velocity).meters

  val vehicleSpec = VehicleSpec.Predefined.Sedan
  val testVehicle = VirtualVehicle(
    gauges = VehicleGauges(
      position = initialPosition,
      velocity = initialVelocity,
      acceleration = 0.0,
      steeringAngle = 0.0,
      heading = initialHeading,
      geometry =
        Vehicle.calcGeometry(initialPosition, initialHeading, vehicleSpec)
    ),
    spec = vehicleSpec,
    targetVelocity = 0.0,
    spawnTime = 0
  )

  val testLane: DirectedLane =
    DirectedLane(LaneSpec(16.6, 2.5))(initialPosition, endPosition)
  val testDriver = CrashTestDriver(testVehicle, testLane, testLane)

  def printReuslt(expectedArrival: Long, result: VehicleState): Unit = {
    println(s"""
             |Expected arrival: ${expectedArrival}
             |Actual arrival:   ${result.currentTime}
             |Distance covered: ${result.distanceCovered}
             |Real duration:    ${result.duration.toUnit(TimeUnit.SECONDS)}
             |""".stripMargin)
  }

  var duration = Duration.Zero
  @tailrec
  final def tryMove(maxIterations: Int = 5 * 1000,
                    timeStep: Double = TickSource.timeStepSeconds)(
      driver: CrashTestDriver,
      currentTime: Long = 0L,
      iteration: Int = 0,
      distanceCovered: Dimension = 0.meters): Try[VehicleState] = {

    if (iteration == 0) {
      duration = timeStep.seconds
    } else {
      duration += timeStep.seconds
    }

    if (iteration > maxIterations) {
      Failure(new RuntimeException("Exceeded max iterations threshold"))
    } else if (distanceCovered >= expectedDistance) {
      Success(
        VehicleState(
          driver.vehicle,
          distanceCovered,
          duration.plus(Duration.Zero),
          currentTime
        ))
    } else {
      val positionBefore = driver.vehicle.position

      val newState = driver
        .prepareToMove()
        .withVehicle {
          driver.vehicle
            .move(currentTime, timeStep)
        }
      val distance = positionBefore.distance(newState.vehicle.position)

      tryMove(maxIterations, timeStep)(
        driver = newState,
        currentTime = currentTime + timeStep.seconds.toMillis,
        iteration = iteration + 1,
        distanceCovered = distanceCovered + distance)
    }
  }

  "virtual vehicle" when {
    "no schedule" must {
      "follow lane" when {
        "accelerating " in {
          val accelerationDuration = (vehicleSpec.maxVelocity - initialVelocity) / vehicleSpec.maxAcceleration
          val accelerationDistance = (vehicleSpec.maxAcceleration + initialVelocity) / 2 * accelerationDuration
          val cruiseDistance = expectedDistance - accelerationDistance.meters
          val cruiseDuration = cruiseDistance.asMeters / vehicleSpec.maxVelocity
          val expectedArrival =
            (accelerationDuration + cruiseDuration).seconds.toMillis

          val result =
            tryMove()(
              testDriver
                .modify(_.vehicle)
                .using(_.maxAccelerationAndTargetVelocity)
            )
          assert(result.isSuccess)
          val distance = result.get.vehicle.position
            .distance(endPosition)
          printReuslt(expectedArrival, result.get)
          assert(
            (result.get.currentTime - expectedArrival).abs <= TickSource.timeStep.toMillis)
          assert(distance <= distancePrecision(result.get.vehicle.velocity))
        }
        "constant velocity " in {
          val expectedDuration = expectedDistance.asMeters / vehicleSpec.maxVelocity
          val expectedArrival = expectedDuration.seconds.toMillis

          val result =
            tryMove()(
              testDriver
                .modify(_.vehicle)
                .using(
                  _.withVelocity(vehicleSpec.maxVelocity).withSteadyVelocity)
            )

          assert(result.isSuccess)
          val distanceDelta = result.get.vehicle.position.distance(endPosition)
          printReuslt(expectedArrival, result.get)
          assert(
            (result.get.currentTime - expectedArrival).abs <= TickSource.timeStep.toMillis)
          assert(
            distanceDelta <= distancePrecision(result.get.vehicle.velocity))
        }
      }
    }

    "with schedule" must {
      "follow lane" when {
        "constant velocity" in {
          val initialVelocity = testLane.spec.speedLimit
          val expectedDuration = expectedDistance.asMeters / initialVelocity
          val accelerationSchedule = AccelerationProfile(
            AccelerationEvent(0, expectedDuration.seconds)
          ).toAccelerationSchedule(0)

          val driver = testDriver.withVehicle(
            testVehicle
              .copy()
              .withVelocity(initialVelocity)
              .withAccelerationSchedule(Some(accelerationSchedule))
          )
          val result = tryMove()(driver)
          printReuslt(expectedDuration.seconds.toMillis, result.get)

          assert(result.isSuccess)
          val remaining = result.toOption.flatMap { res =>
            res.vehicle.accelerationSchedule
              .map(_.calcRemaining(res.currentTime, res.vehicle.velocity))
          }
          val precision = distancePrecision(result.get.vehicle.velocity)
          println(
            "time step distance: " + TickSource.timeStepSeconds * result.get.vehicle.velocity)
          println(s"remaining ${remaining}")
          println(
            s"state: ${result.get.currentTime} :: ${result.get.distanceCovered} :: ${result.get.vehicle.gauges}")
          val distanceFromExpected =
            result.get.vehicle.position.distance(endPosition)
          assert(distanceFromExpected <= precision)
          println(result.get.vehicle.accelerationSchedule)
          assert(remaining.isEmpty || remaining.get._2 <= precision)
        }
        "accelerating" in {}
        "decelerating" in {}
      }
    }
  }
}
