package pl.edu.agh.wmazur.avs.model.entity.utils

import mikera.vectorz._
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.Angle

import scala.annotation.tailrec
import scala.language.implicitConversions

object MathUtils {
  private val defaultMultiply = Math.pow(10, 4)

  def roundDouble(v: Double, precision: Double): Double =
    (v * precision).round * 1.0 / precision

  implicit def double2Float(v: Double): Float = {
    roundDouble(v, defaultMultiply).toFloat
  }

  implicit class FloatUtils(float: Float) {
    def isZero: Boolean = float <= 0.00000001f

  }

  implicit class DoubleUtils(double: Double) {
    def roundToPosition(position: Int = 2): Double = {
      val multiply = position match {
        case 2 => defaultMultiply
        case p => Math.pow(10, p)
      }
      roundDouble(double, multiply)
    }

    def isZero: Boolean = double <= 0.00000001

    def roundFloat: Float = double2Float(double)
  }

  implicit class AVectorRound(vec: AVector) {
    def round(position: Int = 2): AVector = {
      val multiply = position match {
        case 2 => defaultMultiply
        case p => Math.pow(10, p)
      }

      vec match {
        case v: Vector1 => v.setValues(roundDouble(v.x, multiply))
        case v: Vector2 =>
          v.setValues(roundDouble(v.x, multiply), roundDouble(v.y, multiply))
        case v: Vector3 =>
          v.setValues(roundDouble(v.x, multiply),
                      roundDouble(v.y, multiply),
                      roundDouble(v.z, multiply))
        case v: Vector4 =>
          v.setValues(roundDouble(v.x, multiply),
                      roundDouble(v.y, multiply),
                      roundDouble(v.z, multiply),
                      roundDouble(v.t, multiply))
      }
      vec
    }
  }

  def withConstraint[T](value: T, min: T, max: T)(implicit num: Numeric[T]): T =
    value match {
      case v if num.gt(v, max) => max
      case v if num.lt(v, min) => min
      case v                   => v
    }
  @tailrec
  def recenter[T](inputValue: T, minValue: T, maxValue: T)(
      implicit num: Numeric[T]): T = {
    val range = num.minus(maxValue, minValue)
    inputValue match {
      case _ if num.lt(inputValue, minValue) =>
        recenter(num.plus(inputValue, range), minValue, maxValue)
      case _ if num.gt(inputValue, maxValue) =>
        recenter(num.min(inputValue, range), minValue, maxValue)
      case _ => inputValue
    }
  }

  val Pi2: Double = Math.PI * 2

  def boundedAngle(angle: Double): Double = {
    angle - (angle / Pi2).floor * Pi2
  }
}
