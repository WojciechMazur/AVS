package pl.edu.agh.wmazur.avs.model.entity.vehicle

import java.util.concurrent.TimeUnit

import mikera.vectorz.Vector2
import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.utils.MathUtils.DoubleUtils
import pl.edu.agh.wmazur.avs.model.entity.vehicle.AccelerationProfile.AccelerationEvent
import pl.edu.agh.wmazur.avs.model.entity.vehicle.AccelerationSchedule.AccelerationTimestamp
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{
  Acceleration,
  Velocity
}
import pl.edu.agh.wmazur.avs.model.entity.intersection.reservation.ReservationArray.Timestamp

import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

object MaxAccelerationReservationChecker {

  def check(time: Timestamp,
            timeEnd: Timestamp,
            velocity: Velocity,
            velocityEnd: Velocity,
            velocityMax: Velocity,
            distanceTotal: Dimension,
            acceleration: Acceleration,
            deceleration: Acceleration): Try[AccelerationSchedule] = {

    val params = Params(time,
                        timeEnd,
                        velocity,
                        velocityEnd,
                        velocityMax,
                        distanceTotal,
                        acceleration,
                        deceleration)

    Try(makeCheck(params))
      .filter(isScheduleValid(params, _))
  }

  /**
    * The objective is to find an acceleration schedule such that
    * the vehicle can arrive at the intersection at timeEnd at
    * the velocity vEnd, after traveling a distance of dTotal and
    * a time (timeEnd-time1).
    *
    *  To help finding the solutions, we utilize the velocity-time graph.
    *  we consider the rectangle spanned by 1) the starting point
    *  p1, 2) the ending point pEnd, 3) accel and decel.
    *  We call the point at the top of the rectangle pUp
    *  and the point at the bottom of the rectangle bDown
    *
    *
    */
  private def makeCheck(p: Params): AccelerationSchedule = {
    None match {
      case _ if p.time < p.timeEnd => whenAfterEndTime(p)
      case _                       => whenBeforeEndTime(p)
    }
  }

  private case class pParams(t14: Double,
                             t15: Double,
                             t24: Double,
                             t25: Double,
                             vDown: Double,
                             vUp: Double,
                             tTotal: Double)

  private def whenBeforeEndTime(p: Params): AccelerationSchedule = {
    None match {
      case _ if p.t14.isZero =>
        assert(p.velocity isEqual p.vDown)
        assert(p.velocityEnd isEqual p.vUp)
        assert(p.t14.isZero && p.t25.isZero)

        val areaRec = p.timeTotal * (p.velocity + p.velocityEnd) / 2
        if (p.distanceTotal.asMeters isEqual areaRec) {
          AccelerationProfile(
            AccelerationEvent(p.acceleration, p.timeTotal.millis)
          ).toAccelerationSchedule(p.time)
        } else {
          sys.error(
            "Reservation check failed: can't accelerate linearly to meet the arrival time and the arrival velocity ")
        }

      case _ if p.t14 > 0.0 && p.t24.isZero =>
        val areaL = p.timeTotal * (p.velocity + p.velocityEnd) / 2

        if (p.distanceTotal.asMeters isEqual areaL) {
          AccelerationProfile(
            AccelerationEvent(p.deceleration, p.timeTotal.millis)
          ).toAccelerationSchedule(p.time)
        } else {
          sys.error(
            "Reservation check failed: can't decelerate linearly to meet the arrival time and the arrival velocity (Case 2)")
        }

      case _ if p.t14 > 0.0 && p.t24 > 0 => whenDistanceCheckNeeded(p)
      case _ if p.t14 > 0.0 && p.t24 < 0 =>
        sys.error(
          "Reservation check failed: can't decelerate to final velocity (Case 7)")
      case _ =>
        sys.error(
          " Reservation check failed: can't accelerate to final velocity (Case 6)")
    }
  }

  private def whenDistanceCheckNeeded(p: Params): AccelerationSchedule = {
    val (remainingArea, trapezoids) = None match {
      case _ if p.velocity > p.velocityEnd => whenNeedToSlowDown(p)
      case _ if p.velocity < p.velocityEnd => whenNeedToSpeedUp(p)
      case _                               => whenNeedToMaintainSpeed(p)
    }

    findPartialTrapezoid(trapezoids, remainingArea)
      .map(makeAccelerationSchedule(p, _))
      .getOrElse(
        sys.error("Reservation check failed: distance too large (Case 3,4,5)"))
  }

  private def whenNeedToSlowDown(p: Params): (Double, List[Trapezoid]) = {
    val t3x = (p.velocityEnd - p.velocity) / p.deceleration
    val t3 = p.timeTotal - t3x

    val area = p.area0

    val (remainingArea, lowerTrapezoid) = None match {
      case _ if p.vDown >= 0.0 =>
        val areaL = p.t14 * (p.velocity + p.vDown) / 2
        val areaR = p.t15 * (p.vDown + p.velocityEnd) / 2

        val remainingArea =
          calcRemainingArea(area, areaL + areaR, "distance to small (Case 5a)")

        val trapezoid = Trapezoid(Vector2.of(p.time + p.t14, p.vDown),
                                  height = p.velocityEnd - p.vDown,
                                  widthLower = 0.0,
                                  widthUpper = t3,
                                  refDelta = p.t14 - t3x)
        (remainingArea, trapezoid)

      case _ if p.velocity > 0 =>
        val areaL = p.t11 * p.velocity / 2
        val areaR = p.t13 * p.velocityEnd / 2

        val remainingArea =
          calcRemainingArea(area, areaL + areaR, "distance to small (Case 5b)")

        val trapezoid = Trapezoid(Vector2.of(p.time + p.t11, 0.0),
                                  height = p.velocityEnd,
                                  widthLower = p.t12,
                                  widthUpper = t3,
                                  refDelta = p.t11 - t3x)
        (remainingArea, trapezoid)
      case _ =>
        val areaL = p.t11 * p.velocity / 2
        val remainingArea =
          calcRemainingArea(area, areaL, "distance to small (Case 5c)")

        val trapezoid = Trapezoid(Vector2.of(p.time + t3x, p.velocityEnd),
                                  height = p.velocity - p.velocityEnd,
                                  widthLower = t3,
                                  widthUpper = t3,
                                  refDelta = t3x)
        (remainingArea, trapezoid)
    }

    val middleTrapezoid = Trapezoid(Vector2.of(p.time + t3x, p.velocityEnd),
                                    height = p.velocity - p.velocityEnd,
                                    widthLower = t3,
                                    widthUpper = t3,
                                    refDelta = t3x)

    val upperOptTrapezoid = None match {
      case _ if p.vUp <= p.velocityMax =>
        Some {
          Trapezoid(Vector2.of(p.time, p.velocity),
                    height = p.vUp - p.velocity,
                    widthLower = t3,
                    widthUpper = 0.0,
                    refDelta = -p.t24)
        }
      case _ if p.velocityEnd < p.velocityMax =>
        Some {
          Trapezoid(Vector2.of(p.time, p.velocity),
                    height = p.velocityMax - p.velocity,
                    widthLower = t3,
                    widthUpper = p.t22,
                    refDelta = -p.t21)
        }
      case _ => None
    }

    val trapezoids = Some(lowerTrapezoid) :: Some(middleTrapezoid) :: upperOptTrapezoid :: Nil
    (remainingArea, trapezoids.flatten)
  }

  private def whenNeedToSpeedUp(p: Params): (Double, List[Trapezoid]) = {
    val t3x = (p.velocityEnd - p.velocity) / p.acceleration
    val t3 = p.timeTotal - t3x

    val (remainingArea, lowerTrapezoid) = None match {
      case _ if p.vDown >= 0.0 =>
        val areaL = p.t14 * (p.velocity + p.vDown) / 2
        val areaR = p.t15 * (p.vDown + p.velocityEnd) / 2

        val remainingArea =
          calcRemainingArea(p.area0,
                            areaL + areaR,
                            "distance to small (Case 4a)")

        val trapezoid = Trapezoid(Vector2.of(p.time + p.t14, p.vDown),
                                  height = p.velocity - p.vDown,
                                  widthLower = 0.0,
                                  widthUpper = t3,
                                  refDelta = p.t14)
        (remainingArea, trapezoid)

      case _ if p.velocity > 0 =>
        val areaL = p.t11 * p.velocity / 2
        val areaR = p.t13 * p.velocityEnd / 2

        val remainingArea = calcRemainingArea(p.area0,
                                              areaL + areaR,
                                              "distance to small (Case 4b)")

        val trapezoid = Trapezoid(Vector2.of(p.time + p.t11, 0.0),
                                  height = p.velocity,
                                  widthLower = p.t12,
                                  widthUpper = t3,
                                  refDelta = p.t11)
        (remainingArea, trapezoid)

      case _ =>
        val areaR = p.t13 * p.velocityEnd / 2
        val remainingArea =
          calcRemainingArea(p.area0, areaR, "distance to small (Case 4c)")

        val trapezoid = Trapezoid(Vector2.of(p.time + p.t11, 0.0),
                                  height = p.velocity,
                                  widthLower = p.t12,
                                  widthUpper = t3,
                                  refDelta = p.t11)
        (remainingArea, trapezoid)
    }

    val middleTrapezoid = Trapezoid(Vector2.of(p.time, p.velocity),
                                    height = p.velocityEnd - p.velocity,
                                    widthLower = t3,
                                    widthUpper = t3,
                                    refDelta = -t3x)

    val upperOptTrapezoid = None match {
      case _ if p.vUp <= p.velocityMax =>
        Some {
          Trapezoid(Vector2.of(p.time + t3x, p.velocityEnd),
                    height = p.vUp - p.velocityEnd,
                    widthLower = t3,
                    widthUpper = 0.0,
                    refDelta = t3x - p.t24)
        }
      case _ if p.velocityEnd < p.velocityMax =>
        Some {
          Trapezoid(Vector2.of(p.time + t3x, p.velocityEnd),
                    height = p.vUp - p.velocityEnd,
                    widthLower = t3,
                    widthUpper = p.t22,
                    refDelta = t3x - p.t21)
        }
      case _ => None
    }

    val trapezoids = Some(lowerTrapezoid) :: Some(middleTrapezoid) :: upperOptTrapezoid :: Nil
    (remainingArea, trapezoids.flatten)
  }

  private def whenNeedToMaintainSpeed(p: Params): (Double, List[Trapezoid]) = {

    val (remainingArea, lowerOptTrapezoid) = None match {
      case _ if p.vDown >= 0.0 =>
        val areaL = p.t14 * (p.velocity + p.vDown) / 2
        val areaR = p.t15 * (p.vDown + p.velocityEnd) / 2

        val remainingArea =
          calcRemainingArea(p.area0,
                            areaL + areaR,
                            "distance to small (Case 3a)")

        val trapezoid = Trapezoid(Vector2.of(p.time + p.t14, p.vDown),
                                  height = p.velocity - p.vDown,
                                  widthLower = 0.0,
                                  widthUpper = p.timeTotal,
                                  refDelta = p.t14)
        (remainingArea, Some(trapezoid))

      case _ if p.velocity > 0 =>
        val areaL = p.t11 * p.velocity / 2
        val areaR = p.t13 * p.velocityEnd / 2

        val remainingArea = calcRemainingArea(p.area0,
                                              areaL + areaR,
                                              "distance to small (Case 3b)")

        val trapezoid = Trapezoid(Vector2.of(p.time + p.t11, 0.0),
                                  height = p.velocity,
                                  widthLower = p.t12,
                                  widthUpper = p.timeTotal,
                                  refDelta = p.t11)
        (remainingArea, Some(trapezoid))

      case _ =>
        (p.area0, None)
    }

    val upperOptTrapezoid = None match {
      case _ if p.vUp <= p.velocityMax =>
        Some {
          Trapezoid(Vector2.of(p.time, p.velocity),
                    height = p.vUp - p.velocity,
                    widthLower = p.timeTotal,
                    widthUpper = 0.0,
                    refDelta = -p.t24)
        }
      case _ if p.velocityEnd < p.velocityMax =>
        Some {
          Trapezoid(Vector2.of(p.time, p.velocity),
                    height = p.velocityMax - p.velocity,
                    widthLower = p.timeTotal,
                    widthUpper = p.t22,
                    refDelta = -p.t21)
        }
      case _ => None
    }

    val trapezoids = lowerOptTrapezoid :: upperOptTrapezoid :: Nil
    (remainingArea, trapezoids.flatten)
  }

  private def whenAfterEndTime(p: Params): AccelerationSchedule = {
    assert(p.time != p.timeEnd)
    if (p.distanceTotal > 0.0) {
      sys.error(
        "Reservation check failed: distance is not zero when there is no time to move")
    } else {
      assert(p.distanceTotal.asMeters.isZero)
      if (p.velocity isEqual p.velocityEnd) {
        AccelerationSchedule(
          AccelerationTimestamp(0.0, p.time, p.time) :: Nil
        )
      } else {
        sys.error(
          "Reservation check failed: distance is not zero when there is no time to change velocity")
      }
    }
  }

  private def findPartialTrapezoid(
      trapezoids: List[Trapezoid],
      remainingArea: Acceleration): Option[(Vector2, Vector2)] = {

    val (_, solution) =
      trapezoids.foldLeft((remainingArea, Option.empty[(Vector2, Vector2)])) {
        case ((area, res @ Some(_)), _) => (area, res)

        case ((area, None), trapezoid: Trapezoid)
            if remainingArea.isEqual(trapezoid.area) ||
              remainingArea < trapezoid.area =>
          val optSolution = Try(calcPartialTrapezoid(trapezoid, area)) match {
            case Success(value) => Some(value)
            case Failure(exception) =>
              System.err.println(exception.getMessage)
              None
          }
          (area, optSolution)

        case ((area, None), trapezoid) =>
          (area - trapezoid.area, None)
      }

    solution
  }

  private def calcPartialTrapezoid(
      p: MaxAccelerationReservationChecker.Trapezoid,
      area: Double): (Vector2, Vector2) = {
    assert(p.height >= 0.0)
    assert(
      p.widthLower >= 0.0 && p.widthUpper > 0 ||
        p.widthLower > 0.0 && p.widthUpper >= 0.0
    )
    assert(area isEqual (p.height * (p.widthLower + p.widthUpper) / 2))

    def buildLine(w: Double,
                  h: Double,
                  x: Double,
                  refPoint: Vector2): (Vector2, Vector2) = {
      val p1x = refPoint.x - x
      val p1y = refPoint.y + h
      (
        Vector2.of(p1x, p1y),
        Vector2.of(p1x + w, p1y)
      )
    }

    None match {
      case _ if area.isZero =>
        buildLine(p.widthLower, 0.0, 0.0, p.referencePoint)

      case _ if area isEqual p.area =>
        buildLine(p.widthUpper, p.height, p.refDelta, p.referencePoint)

      case _ if area >= 0.0 && p.area >= 0.0 =>
        if (p.widthUpper == p.widthLower) {
          val h0 = area / p.widthLower
          buildLine(p.widthLower,
                    area / p.widthLower,
                    h0 * p.refDelta / p.height,
                    p.referencePoint)
        } else {
          val h0 = Math.sqrt(
            Math.pow(p.widthLower, 2) *
              Math.pow(p.height, 2) + {
              (p.widthUpper - p.widthLower) * (2 * area * p.height) -
                (p.widthLower * p.height)
            })
          val w0 = (p.widthUpper - p.widthLower) * h0 / p.height + p.widthLower
          val x0 = h0 * p.refDelta / p.height
          buildLine(w0, h0, x0, p.referencePoint)
        }
      case _ =>
        sys.error("Error in LevelOffReservationCheck: calcPartialTrapezoid")
    }
  }

  private def makeAccelerationSchedule(
      p: Params,
      vec: (Vector2, Vector2)): AccelerationSchedule = {
    val (p1, p2) = vec

    val has2ndPhase = p1.x isEqual p2.x
    val p1Time = p1.x.seconds.toMillis
    val p2Time = p2.x.seconds.toMillis

    val builder = AccelerationSchedule.Builder()

    if (p1Time == p.time) {
      assert(p1.y isEqual p.velocity)
    } else {

      if (p1.y < p.velocity) {
        builder.add(p.deceleration, p.time)
      } else {
        builder.add(p.deceleration, p.time)
      }
    }

    if (has2ndPhase) {
      builder.add(0.0, p1Time)
    }

    if (p2Time == p.timeEnd) {
      assert(p2.y isEqual p.velocityEnd)
    } else {
      if (p2.y < p.velocityEnd) {
        builder.add(p.acceleration, p2Time)
      } else {
        assert(p.velocityEnd < p2.y)
        builder.add(p.deceleration, p2Time)
      }
    }
    builder.add(0.0, p.timeEnd)

    builder.build
  }

  private def calcRemainingArea(area0: Double,
                                area1: Double,
                                errMsg: String) = {
    val remainingArea = area0 - area1 match {
      case _area if _area.isZero => 0.0
      case _area                 => _area
    }
    assert(remainingArea >= 0.0, s"Reservation check failed: $errMsg")
    remainingArea
  }

  private def isScheduleValid(params: Params,
                              schedule: AccelerationSchedule): Boolean = {
    def check(condition: Boolean, msg: => String): Unit = {
      if (!condition) {
        System.err.println(
          "MaxAcclerationReservationChecker :: schedule validation :: " + msg)
      }
    }

    val hasValidSize = schedule.timestamps.nonEmpty && schedule.timestamps.size <= 4

    val correctInititalTime = schedule.timestamps.head.timeStart == params.time

    val hasNonNegetiveDurations: Boolean = schedule.timestamps
      .forall(_.duration.length >= 0)

    val (finalVelocity, finalDistance, hasValidVelocities) = schedule.timestamps
      .foldLeft((params.velocity, 0.0, true)) {
        case ((velocity, distance, isValid), event) =>
          val duration = event.duration.toUnit(TimeUnit.SECONDS)
          val newVelocity = velocity + event.acceleration * duration
          val newDistance = distance + (velocity + newVelocity) / 2 * duration
          val result = newVelocity < params.velocityMax ||
            newVelocity.isEqual(params.velocityMax)

          (newVelocity, newDistance, isValid && result)
      }

    val correctTime = schedule.timestamps.last.timeEnd == params.timeEnd

    val correctEndingVelocity = finalVelocity.isEqual(params.velocityEnd)

    val corectFinalDistance = finalDistance.isEqual(params.distanceTotal)

    check(hasValidSize, "Invalid acceleration schedule size")
    check(correctInititalTime, "Invalid acceleration initial time")
    check(hasNonNegetiveDurations, "Duration cannot be negative")
    check(hasValidVelocities, "Velocity is greater then maximal velocity")
    check(correctTime, "Incorect ending time")
    check(correctEndingVelocity, "Invalid end velocity")
    check(corectFinalDistance, "Incorect total distance")

    hasValidSize &&
    correctInititalTime &&
    hasNonNegetiveDurations &&
    hasValidVelocities &&
    correctTime &&
    correctEndingVelocity &&
    corectFinalDistance
  }

  private case class Params(
      time: Timestamp,
      timeEnd: Timestamp,
      velocity: Velocity,
      velocityEnd: Velocity,
      velocityMax: Velocity,
      distanceTotal: Dimension,
      acceleration: Acceleration,
      deceleration: Acceleration
  ) {
    // format: off
    assert(time < timeEnd)
    assert(velocity >= 0.0)
    assert(velocity.isEqual(velocityMax) || velocity < velocityMax)
    assert(velocityEnd >= 0.0)
    assert(velocityEnd <= velocityMax)
    assert(distanceTotal >= 0.0)
    assert(acceleration > 0)
    assert(deceleration < 0)

    val timeTotal: Double = (timeEnd - time).millis.toUnit(TimeUnit.SECONDS)

    lazy val t11: Double = -velocity / deceleration
    lazy val t12: Double = timeTotal - t11 - t13
    lazy val t13: Double = velocityEnd / acceleration
    lazy val t14: Double = (velocityEnd - acceleration * timeTotal - velocity) / (deceleration - acceleration)
    lazy val t15: Double = timeTotal - t14
    lazy val vDown: Double = velocity + deceleration * t14

    lazy val t21: Double = (velocityMax - velocity) / acceleration
    lazy val t22: Double = timeTotal - t21 - t23
    lazy val t23: Double = (velocityEnd - velocityMax) / deceleration
    lazy val t24: Double = (velocityEnd - deceleration * timeTotal - velocity) / (deceleration - acceleration)
    lazy val t25: Double = timeTotal - t24
    lazy val vUp: Double = velocity + deceleration * t24

    val area0: Double = distanceTotal.asMeters

    // format: on
  }
  private case class Trapezoid(
      referencePoint: Vector2,
      height: Double,
      widthLower: Double,
      widthUpper: Double,
      refDelta: Double
  ) {
    lazy val area: Double = height * (widthLower + widthUpper) / 2
  }

}
