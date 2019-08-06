package pl.edu.agh.wmazur.avs.model.entity.vehicle

import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.vehicle.AccelerationProfile.AccelerationEvent
import pl.edu.agh.wmazur.avs.model.entity.vehicle.AccelerationSchedule.AccelerationTimestamp
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{
  Acceleration,
  Velocity
}
import pl.edu.agh.wmazur.avs.simulation.reservation.ReservationArray.Timestamp
import pl.edu.agh.wmazur.avs.model.entity.utils.MathUtils.DoubleUtils

import scala.concurrent.duration._
import scala.util.{Success, Try}

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

    val result = if (d > 0.0) {
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
      }
      sys.error(
        "distance is zero and the current velocity is larger than the maximum final velocity")
    }

    Try(result)
  }

  private object EdgeCases {
    def case0(params: Parameters): Result = {
      Result(
        params.initialTime,
        params.velocity,
        AccelerationSchedule(params.initialTime, 0.0)
      )
    }
    def case1(params: Parameters): Result = {
      //velocity == maxVelocity == finalVelocity
      val timeTotal =
        (params.distanceTotal.meters / params.finalVelocity).seconds.toMillis

      val finalTime = params.initialTime + timeTotal
      val schedule = AccelerationSchedule(
        List(
          AccelerationTimestamp(0.0, params.initialTime),
          AccelerationTimestamp(0.0, finalTime)
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

      def case2a = {

        val distanceCruise = params.distanceTotal.meters - distanceAccel
        val timeCruise = distanceCruise / params.maxVelocity

        val schedule = AccelerationProfile {
          List(
            AccelerationEvent(params.maxAcceleration, Duration.Zero),
            AccelerationEvent(0, timeAccel.seconds),
            AccelerationEvent(0, timeCruise.seconds)
          )
        }.toAccelerationSchedule(params.initialTime)
        Result(
          arrivalTime = params.initialTime + (timeAccel + timeCruise).seconds.toMillis,
          arrivalVelocity = params.finalVelocity,
          accelerationSchedule = schedule)
      }
      def case2b = {
        val schedule = AccelerationProfile {
          List(
            AccelerationEvent(params.maxAcceleration, Duration.Zero),
            AccelerationEvent(0, timeAccel.seconds),
          )
        }.toAccelerationSchedule(params.initialTime)
        Result(arrivalTime = params.initialTime + timeAccel.seconds.toMillis,
               arrivalVelocity = params.finalVelocity,
               accelerationSchedule = schedule)
      }

      None match {
        case _ if distanceAccel < params.distanceTotal.meters => case2a
        case _ if distanceAccel isEqual params.distanceTotal  => case2b
        case _                                                => ??? //TODO maxVEndForCase2AndCase5
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

      def case3a = {
        val distanceCruise = params.distanceTotal.meters - distanceDecel
        val timeCruise = distanceCruise / params.maxVelocity
        Result(
          params.initialTime + (timeCruise + timeDecel).seconds.toMillis,
          params.finalVelocity,
          AccelerationProfile(
            AccelerationEvent(0.0, Duration.Zero),
            AccelerationEvent(params.maxDeceleration, timeCruise.seconds),
            AccelerationEvent(0.0, timeDecel.seconds)
          ).toAccelerationSchedule(params.initialTime)
        )
      }

      def case3b = {
        Result(
          params.initialTime + timeDecel.seconds.toMillis,
          params.finalVelocity,
          AccelerationProfile(
            AccelerationEvent(params.maxDeceleration, Duration.Zero),
            AccelerationEvent(0.0, timeDecel.seconds)
          ).toAccelerationSchedule(params.initialTime)
        )
      }

      None match {
        case _ if distanceDecel < params.distanceTotal.meters => case3a
        case _ if distanceDecel isEqual params.distanceTotal  => case3b
        case _                                                => sys.error("Distance to small")
      }
    }

    def case4(params: Parameters) = {
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
        val distanceCruise = params.distanceTotal.meters - distanceAccel - distanceDecel
        val timeCruise = distanceCruise / params.maxVelocity
        Result(
          arrivalTime = params.initialTime + (timeAccel + timeCruise + timeDecel).seconds.toMillis,
          arrivalVelocity = params.finalVelocity,
          accelerationSchedule = AccelerationProfile(
            AccelerationEvent(params.maxAcceleration, Duration.Zero),
            AccelerationEvent(0.0, timeAccel.seconds),
            AccelerationEvent(params.maxDeceleration, timeCruise.seconds),
            AccelerationEvent(0.0, timeDecel.seconds)
          ).toAccelerationSchedule(params.initialTime)
        )
      }

      def case4b = {
        val delta = (params.maxAcceleration * params.finalVelocity * params.finalVelocity - params.maxDeceleration * params.velocity * params.velocity - 2 * params.maxAcceleration * params.maxDeceleration * params.distanceTotal.meters) / (params.maxAcceleration - params.maxDeceleration)
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
            AccelerationEvent(params.maxAcceleration, Duration.Zero),
            AccelerationEvent(params.maxDeceleration, timeAccel.seconds),
            AccelerationEvent(0.0, timeDecel.seconds)
          ).toAccelerationSchedule(params.initialTime)
        )
      }

      None match {
        case _ if distanceAccel + distanceDecel < params.distanceTotal.meters =>
          case4a
        case _ => case4b
      }
    }

    def case5(params: Parameters) = {
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

      val timeDecel = (params.finalVelocity - params.velocity) / params.maxDeceleration
      val distanceDecel = timeDecel * (params.maxVelocity + params.finalVelocity) / 2

      def case5a = {
        val distanceCruise = params.distanceTotal.meters - distanceAccel - distanceDecel
        val timeCruise = distanceCruise / params.maxVelocity

        Result(
          params.initialTime + (timeAccel + timeCruise + timeDecel).seconds.toMillis,
          params.finalVelocity,
          AccelerationProfile(
            AccelerationEvent(params.maxAcceleration, Duration.Zero),
            AccelerationEvent(0.0, timeAccel.seconds),
            AccelerationEvent(params.maxDeceleration, timeCruise.seconds),
            AccelerationEvent(0.0, timeDecel.seconds)
          ).toAccelerationSchedule(params.initialTime)
        )
      }

      def case5bcd = {
        val delta = (params.maxAcceleration * params.finalVelocity * params.finalVelocity - params.maxDeceleration * params.velocity * params.velocity - 2 * params.maxAcceleration * params.maxDeceleration * params.distanceTotal.meters) / (params.maxAcceleration - params.maxDeceleration)
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
              AccelerationEvent(params.maxAcceleration, Duration.Zero),
              AccelerationEvent(params.maxDeceleration, timeAccel.seconds),
              AccelerationEvent(0.0, timeDecel.seconds)
            ).toAccelerationSchedule(params.initialTime)
          )
        }

        def case5c = {
          assert(distanceAccel.isEqual(params.distanceTotal))
          Result(
            params.initialTime + timeAccel.seconds.toMillis,
            params.finalVelocity,
            AccelerationProfile(
              AccelerationEvent(params.maxAcceleration, Duration.Zero),
              AccelerationEvent(0.0, timeAccel.seconds)
            ).toAccelerationSchedule(params.initialTime)
          )
        }

        None match {
          case _ if params.finalVelocity < velocity       => case5b
          case _ if params.finalVelocity isEqual velocity => case5c
          case _                                          => ??? //TODO maxVEndForCase2AndCase5
        }
      }

      None match {
        case _ if distanceAccel + distanceDecel < params.distanceTotal.meters =>
          case5a
        case _ => case5bcd
      }
    }

    def case6(params: Parameters) = {
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

      val timeDecel = (params.finalVelocity - params.velocity) / params.maxDeceleration
      val distanceDecel = timeDecel * (params.maxVelocity + params.finalVelocity) / 2

      def case6a = {
        val distanceCruise = params.distanceTotal.meters - distanceAccel - distanceDecel
        val timeCruise = distanceCruise / params.maxVelocity

        Result(
          params.initialTime + (timeAccel + timeCruise + timeDecel).seconds.toMillis,
          params.finalVelocity,
          AccelerationProfile(
            AccelerationEvent(params.maxAcceleration, Duration.Zero),
            AccelerationEvent(0.0, timeAccel.seconds),
            AccelerationEvent(params.maxDeceleration, timeCruise.seconds),
            AccelerationEvent(0.0, timeDecel.seconds)
          ).toAccelerationSchedule(params.initialTime)
        )
      }

      def case6bcd = {
        val delta = (params.maxAcceleration * params.finalVelocity * params.finalVelocity - params.maxDeceleration * params.velocity * params.velocity - 2 * params.maxAcceleration * params.maxDeceleration * params.distanceTotal.meters) / (params.maxAcceleration - params.maxDeceleration)
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
              AccelerationEvent(params.maxAcceleration, Duration.Zero),
              AccelerationEvent(params.maxDeceleration, timeAccel.seconds),
              AccelerationEvent(0.0, timeDecel.seconds)
            ).toAccelerationSchedule(params.initialTime)
          )
        }

        def case6c = {
          assert(distanceDecel.isEqual(params.distanceTotal))
          Result(
            params.initialTime + timeDecel.seconds.toMillis,
            params.finalVelocity,
            AccelerationProfile(
              AccelerationEvent(params.maxDeceleration, Duration.Zero),
              AccelerationEvent(0.0, timeDecel.seconds)
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
        case _ if distanceAccel + distanceDecel < params.distanceTotal.meters =>
          case6a
        case _ => case6bcd
      }
    }

  }

}
