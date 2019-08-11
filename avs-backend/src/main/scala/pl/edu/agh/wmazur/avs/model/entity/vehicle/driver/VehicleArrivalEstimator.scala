package pl.edu.agh.wmazur.avs.model.entity.vehicle.driver

import java.util.concurrent.TimeUnit

import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.intersection.reservation.ReservationArray.Timestamp
import pl.edu.agh.wmazur.avs.model.entity.vehicle.AccelerationProfile.AccelerationEvent
import pl.edu.agh.wmazur.avs.model.entity.vehicle.AccelerationSchedule.AccelerationTimestamp
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{
  Acceleration,
  Velocity
}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.{
  AccelerationProfile,
  AccelerationSchedule
}

import scala.concurrent.duration._
import pl.edu.agh.wmazur.avs.model.entity.utils.MathUtils._
import scala.util.Try

object VehicleArrivalEstimator {
  case class Result(arrivalTime: Timestamp,
                    arrivalVelocity: Velocity,
                    accelerationSchedule: AccelerationSchedule)

  case class Parameters(
      initialTime: Timestamp,
      velocity: Velocity,
      distanceTotal: Dimension,
      maxVelocity: Velocity,
      finalVelocity: Velocity,
      maxAcceleration: Acceleration,
      maxDeceleration: Acceleration
  )

  def estimate(params: Parameters): Try[Result] = {
    val d = params.distanceTotal
    val vEnd = params.finalVelocity
    val vMax = params.maxVelocity
    val v = params.velocity

    Try {
      if (d > 0.0) {
        if (vEnd < vMax) {
          if (v < vMax) {
            if (v > vEnd) {
              EdgeCases.case6(params)
            } else {
              if (v < vEnd) {
                EdgeCases.case5(params)
              } else {
                EdgeCases.case4(params)
              }
            }
          } else {
            EdgeCases.case3(params)
          }
        } else {
          if (v < vMax) {
            EdgeCases.case2(params)
          } else {
            EdgeCases.case1(params)
          }
        }
      } else {
        if (v <= vEnd) {
          EdgeCases.case0(params)
        } else {
          sys.error(
            "distance is zero and the current velocity is larger than the maximum final velocity")
        }
      }
    }
  }

  def isValid(params: Parameters, result: Result): Boolean = {
    val (finalDistance, finalVelocity) = result.accelerationSchedule
      .calculateFinalStateAtTime(params.initialTime,
                                 params.velocity,
                                 result.arrivalTime)

    def check1 = finalVelocity.isEqual(result.arrivalVelocity)

    def check2 =
      finalVelocity.isEqual(params.maxVelocity) &&
        finalVelocity <= params.finalVelocity

    def check3 =
      params.velocity.isEqual(params.maxVelocity) ||
        params.velocity < params.maxVelocity

    def timestamps = result.accelerationSchedule.timestamps

    lazy val (_, _, check4) = timestamps.tail
      .foldLeft((params.velocity, timestamps.head, true)) {
        case ((velocity, prev, wasValid), curr) =>
          if (wasValid) {

            val newVelocity = velocity + (curr.timeStart - prev.timeStart).millis
              .toUnit(TimeUnit.SECONDS) * prev.acceleration
            val isValid = !newVelocity.isEqual(params.maxVelocity) &&
              newVelocity > params.maxVelocity
            (newVelocity, curr, isValid)
          } else {
            (velocity, prev, wasValid)
          }
      }

    def check5 = finalDistance.isEqual(params.distanceTotal)

    List(check1, check2, check3, check4, check5)
      .contains(false)
  }

  private object EdgeCases {
    def case0(params: Parameters): Result = {
      Result(
        params.initialTime,
        params.velocity,
        AccelerationSchedule {
          AccelerationTimestamp(0.0, params.initialTime, params.initialTime) :: Nil
        }
      )
    }
    def case1(params: Parameters): Result = {
      //velocity == maxVelocity == finalVelocity
      val timeTotal =
        (params.distanceTotal.asMeters / params.finalVelocity).seconds.toMillis

      val finalTime = params.initialTime + timeTotal
      val schedule = AccelerationSchedule(
        List(
          AccelerationTimestamp(0.0, params.initialTime, finalTime),
          AccelerationTimestamp(0.0, finalTime, finalTime)
        ))
      Result(finalTime, params.finalVelocity, schedule)
    }

    def case2(params: Parameters): Result = {
      // Case 2: v < vMax == vEnd
      //   Case 2a: accelerate to vMax and then maintain the speed
      //   Case 2b: accelerate to vEnd directly
      //   Case 2c: infeasible case; can't accelerate to vEnd
      //            due to distance constraint
      val timeAccel = (params.maxVelocity - params.velocity) / params.maxAcceleration
      val distanceAccel = timeAccel * (params.maxVelocity + params.velocity) / 2

      def case2a: Result = {

        val distanceCruise = params.distanceTotal.asMeters - distanceAccel
        val timeCruise = distanceCruise / params.maxVelocity

        val schedule = AccelerationProfile {
          List(
            AccelerationEvent(params.maxAcceleration, timeAccel.seconds),
            AccelerationEvent(0, timeCruise.seconds),
            AccelerationEvent(0, Duration.Zero)
          )
        }.toAccelerationSchedule(params.initialTime)
        Result(
          arrivalTime = params.initialTime + (timeAccel + timeCruise).seconds.toMillis,
          arrivalVelocity = params.finalVelocity,
          accelerationSchedule = schedule)
      }
      def case2b: Result = {
        val schedule = AccelerationProfile {
          List(
            AccelerationEvent(params.maxAcceleration, timeAccel.seconds),
            AccelerationEvent(0, Duration.Zero),
          )
        }.toAccelerationSchedule(params.initialTime)
        Result(arrivalTime = params.initialTime + timeAccel.seconds.toMillis,
               arrivalVelocity = params.finalVelocity,
               accelerationSchedule = schedule)
      }

      None match {
        case _ if distanceAccel < params.distanceTotal.asMeters => case2a
        case _ if distanceAccel isEqual params.distanceTotal    => case2b
        case _ =>
          estimateMaxFinalVelocity(params) //TODO maxVEndForCase2AndCase5
      }
    }

    def case3(params: Parameters): Result = {
      // Case 3: v1 == vTop > vEndMax
      //   Case 3a: maintain the speed and then decelerate to vEndMax
      //   Case 3b: directly decelerate to vEndMax
      //   Case 3c: infeasible case; can't decelerate to vEndMax
      //            due to distance constraint

      val timeDecel = (params.finalVelocity - params.maxVelocity) / params.maxDeceleration
      val distanceDecel = timeDecel * (params.finalVelocity + params.maxVelocity) / 2

      def case3a: Result = {
        val distanceCruise = params.distanceTotal.asMeters - distanceDecel
        val timeCruise = distanceCruise / params.maxVelocity
        Result(
          params.initialTime + (timeCruise + timeDecel).seconds.toMillis,
          params.finalVelocity,
          AccelerationProfile(
            AccelerationEvent(0.0, timeCruise.seconds),
            AccelerationEvent(params.maxDeceleration, timeDecel.seconds),
            AccelerationEvent(0.0, Duration.Zero)
          ).toAccelerationSchedule(params.initialTime)
        )
      }

      def case3b = {
        Result(
          params.initialTime + timeDecel.seconds.toMillis,
          params.finalVelocity,
          AccelerationProfile(
            AccelerationEvent(params.maxDeceleration, timeDecel.seconds),
            AccelerationEvent(0.0, Duration.Zero)
          ).toAccelerationSchedule(params.initialTime)
        )
      }

      None match {
        case _ if distanceDecel < params.distanceTotal.asMeters => case3a
        case _ if distanceDecel isEqual params.distanceTotal    => case3b
        case _                                                  => sys.error("Distance to small")
      }
    }

    def case4(params: Parameters): Result = {
      // Case 4: v1,vEndMax < vTop  &&  v1 == vEndMax
      //   Case 4a: accelerate to vTop, then maintain the speed, and then
      //            decelerate to vEndMax
      //   Case 4b: accelerate to v2 (v1 <= v2 < vTop), and then
      //            decelerate to vEndMax immediately
      //   Always feasible; there is no degenerated case such as
      //   since dTotal > 0

      val timeAccel = (params.maxVelocity - params.velocity) / params.maxAcceleration
      val distanceAccel = timeAccel * (params.maxVelocity + params.velocity) / 2

      val timeDecel = (params.finalVelocity - params.velocity) / params.maxDeceleration
      val distanceDecel = timeDecel * (params.maxVelocity + params.finalVelocity) / 2

      def case4a = {
        val distanceCruise = params.distanceTotal.asMeters - distanceAccel - distanceDecel
        val timeCruise = distanceCruise / params.maxVelocity
        Result(
          arrivalTime = params.initialTime + (timeAccel + timeCruise + timeDecel).seconds.toMillis,
          arrivalVelocity = params.finalVelocity,
          accelerationSchedule = AccelerationProfile(
            AccelerationEvent(params.maxAcceleration, timeAccel.seconds),
            AccelerationEvent(0.0, timeCruise.seconds),
            AccelerationEvent(params.maxDeceleration, timeDecel.seconds),
            AccelerationEvent(0.0, Duration.Zero)
          ).toAccelerationSchedule(params.initialTime)
        )
      }

      def case4b = {
        val delta = (params.maxAcceleration * params.finalVelocity * params.finalVelocity - params.maxDeceleration * params.velocity * params.velocity - 2 * params.maxAcceleration * params.maxDeceleration * params.distanceTotal.asMeters) / (params.maxAcceleration - params.maxDeceleration)
        assert(delta >= 0.0)
        val velocity = Math.sqrt(delta)
        assert(velocity < params.maxVelocity)

        val timeAccel = (velocity - params.velocity) / params.maxAcceleration
        val distanceAccel = timeAccel * (params.velocity + velocity) / 2

        val timeDecel = (params.finalVelocity - velocity) / params.maxDeceleration
        val distanceDecel = timeDecel * (params.finalVelocity + velocity) / 2

        assert(distanceAccel >= 0 && distanceDecel >= 0.0)
        Result(
          params.initialTime + (timeAccel + timeDecel).seconds.toMillis,
          params.finalVelocity,
          AccelerationProfile(
            AccelerationEvent(params.maxAcceleration, timeAccel.seconds),
            AccelerationEvent(params.maxDeceleration, timeDecel.seconds),
            AccelerationEvent(0.0, Duration.Zero)
          ).toAccelerationSchedule(params.initialTime)
        )
      }

      None match {
        case _
            if distanceAccel + distanceDecel < params.distanceTotal.asMeters =>
          case4a
        case _ => case4b
      }
    }

    def case5(params: Parameters): Result = {
      // Case 5: v1,vEndMax < vTop  &&  v1 < vEndMax
      //   Case 5a: accelerate to vTop, then maintain the speed, and then
      //            decelerate to vEndMax
      //   Case 5b: accelerate to v2 (vEndMax < v2 < vTop), and then
      //            decelerate to vEndMax immediately
      //   Case 5c: accelerate to vEndMax immediately  (vEndMax = v2)
      //   Case 5d: infeasible case; can't accelerate to vEndMax
      //            due to distance constraint  (vEndMax > v2)
      val timeAccel = (params.maxVelocity - params.velocity) / params.maxAcceleration
      val distanceAccel = timeAccel * (params.maxVelocity + params.velocity) / 2

      val timeDecel = (params.finalVelocity - params.maxVelocity) / params.maxDeceleration
      val distanceDecel = timeDecel * (params.maxVelocity + params.finalVelocity) / 2

      def case5a = {
        val distanceCruise = params.distanceTotal.asMeters - distanceAccel - distanceDecel
        val timeCruise = distanceCruise / params.maxVelocity

        Result(
          params.initialTime + (timeAccel + timeCruise + timeDecel).seconds.toMillis,
          params.finalVelocity,
          AccelerationProfile(
            AccelerationEvent(params.maxAcceleration, timeAccel.seconds),
            AccelerationEvent(0.0, timeCruise.seconds),
            AccelerationEvent(params.maxDeceleration, timeDecel.seconds),
            AccelerationEvent(0.0, Duration.Zero)
          ).toAccelerationSchedule(params.initialTime)
        )
      }

      def case5bcd = {
        val delta = (params.maxAcceleration * Math.pow(params.finalVelocity, 2)
          - params.maxDeceleration * Math.pow(params.velocity, 2)
          - 2 * params.maxAcceleration * params.maxDeceleration * params.distanceTotal.asMeters) / (params.maxAcceleration - params.maxDeceleration)
        assert(delta >= 0.0)

        val velocity = Math.sqrt(delta)
        assert(velocity > params.velocity)

        val timeAccel = (velocity - params.velocity) / params.maxAcceleration
        val distanceAccel = timeAccel * (params.velocity + velocity) / 2
        assert(distanceAccel > 0.0)

        def case5b = {
          val timeDecel = (params.finalVelocity - velocity) / params.maxDeceleration
          val distanceDecel = timeDecel * (params.finalVelocity + velocity) / 2
          assert(distanceDecel > 0.0)

          Result(
            params.initialTime + (timeAccel + timeDecel).seconds.toMillis,
            params.finalVelocity,
            AccelerationProfile(
              AccelerationEvent(params.maxAcceleration, timeAccel.seconds),
              AccelerationEvent(params.maxDeceleration, timeDecel.seconds),
              AccelerationEvent(0.0, Duration.Zero)
            ).toAccelerationSchedule(params.initialTime)
          )
        }

        def case5c = {
          assert(distanceAccel.isEqual(params.distanceTotal))
          Result(
            params.initialTime + timeAccel.seconds.toMillis,
            params.finalVelocity,
            AccelerationProfile(
              AccelerationEvent(params.maxAcceleration, timeAccel.seconds),
              AccelerationEvent(0.0, Duration.Zero)
            ).toAccelerationSchedule(params.initialTime)
          )
        }

        None match {
          case _ if params.finalVelocity < velocity       => case5b
          case _ if params.finalVelocity isEqual velocity => case5c
          case _                                          => estimateMaxFinalVelocity(params)
        }
      }

      None match {
        case _
            if distanceAccel + distanceDecel < params.distanceTotal.asMeters =>
          case5a
        case _ => case5bcd
      }
    }

    def case6(params: Parameters): Result = {
      // Case 6: v1,vEndMax < vTop  &&  v1 > vEndMax
      //   Case 6a: accelerate to vTop, then maintain the speed, and then
      //            decelerate to vEndMax
      //   Case 6b: accelerate to v2 (v1 < v2 < vTop), and then
      //            decelerate to vEndMax immediately
      //   Case 6c: decelerate to vEndMax immediately  (v1 = v2)
      //   Case 6d: infeasible case; can't accelerate to vEndMax
      //            due to distance constraint  (v2 < v1)
      val timeAccel = (params.maxVelocity - params.velocity) / params.maxAcceleration
      val distanceAccel = timeAccel * (params.maxVelocity + params.velocity) / 2

      val timeDecel = (params.finalVelocity - params.maxVelocity) / params.maxDeceleration
      val distanceDecel = timeDecel * (params.maxVelocity + params.finalVelocity) / 2

      def case6a: Result = {
        val distanceCruise = params.distanceTotal.asMeters - distanceAccel - distanceDecel
        val timeCruise = distanceCruise / params.maxVelocity

        Result(
          params.initialTime + (timeAccel + timeCruise + timeDecel).seconds.toMillis,
          params.finalVelocity,
          AccelerationProfile(
            AccelerationEvent(params.maxAcceleration, timeAccel.seconds),
            AccelerationEvent(0.0, timeCruise.seconds),
            AccelerationEvent(params.maxDeceleration, timeDecel.seconds),
            AccelerationEvent(0.0, Duration.Zero)
          ).toAccelerationSchedule(params.initialTime)
        )
      }

      def case6bcd: Result = {
        val delta = (params.maxAcceleration * Math.pow(params.finalVelocity, 2) - params.maxDeceleration * Math.pow(
          params.velocity,
          2) - 2 * params.maxAcceleration * params.maxDeceleration * params.distanceTotal.asMeters) / (params.maxAcceleration - params.maxDeceleration)

        assert(delta >= 0.0)

        val velocity = Math.sqrt(delta)
        assert(velocity < params.maxVelocity)
        assert(velocity > params.finalVelocity)

        val timeDecel = (params.finalVelocity - velocity) / params.maxDeceleration
        val distanceDecel = timeDecel * (params.finalVelocity + velocity) / 2

        assert(distanceDecel > 0.0)

        def case6b = {
          val timeAccel = (velocity - params.velocity) / params.maxAcceleration
          val distanceAccel = timeAccel * (params.velocity + velocity) / 2
          assert(distanceAccel > 0.0)

          Result(
            params.initialTime + (timeAccel + timeDecel).seconds.toMillis,
            params.finalVelocity,
            AccelerationProfile(
              AccelerationEvent(params.maxAcceleration, timeAccel.seconds),
              AccelerationEvent(params.maxDeceleration, timeDecel.seconds),
              AccelerationEvent(0.0, Duration.Zero)
            ).toAccelerationSchedule(params.initialTime)
          )
        }

        def case6c = {
          assert(distanceDecel.isEqual(params.distanceTotal))
          Result(
            params.initialTime + timeDecel.seconds.toMillis,
            params.finalVelocity,
            AccelerationProfile(
              AccelerationEvent(params.maxDeceleration, timeDecel.seconds),
              AccelerationEvent(0.0, Duration.Zero)
            ).toAccelerationSchedule(params.initialTime)
          )
        }

        None match {
          case _ if params.velocity < velocity       => case6b
          case _ if params.velocity isEqual velocity => case6c
          case _                                     => sys.error("distance too small")
        }
      }

      None match {
        case _
            if distanceAccel + distanceDecel < params.distanceTotal.asMeters =>
          case6a
        case _ => case6bcd
      }
    }

    def estimateMaxFinalVelocity(params: Parameters): Result = {
      val estimatedEndVelocity = Math.sqrt(
        2 * params.maxAcceleration * params.distanceTotal.asMeters
          + Math.pow(params.velocity, 2)
      )
      val timeAccelerating = (estimatedEndVelocity - params.velocity) / params.maxAcceleration
      assert(estimatedEndVelocity < params.finalVelocity)

      Result(
        params.initialTime + timeAccelerating.seconds.toMillis,
        estimatedEndVelocity,
        AccelerationProfile(
          AccelerationEvent(params.maxAcceleration, timeAccelerating.seconds),
          AccelerationEvent(0.0, Duration.Zero)
        ).toAccelerationSchedule(params.initialTime)
      )
    }
  }

}
