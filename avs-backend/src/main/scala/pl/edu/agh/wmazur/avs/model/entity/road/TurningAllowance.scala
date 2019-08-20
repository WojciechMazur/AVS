package pl.edu.agh.wmazur.avs.model.entity.road

import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.Angle

sealed trait TurningAllowance {
  private val twoPi = 2 * Math.PI
  private val strightThreshold = 15.0.toRadians
  private val leftTurnMaxThreshold = 105.0.toRadians
  private val rightTurnMaxThreshold = 255.0.toRadians

  def canGoStraight: Boolean = false
  def canGoStraight(headingDelta: Angle): Boolean = {
    canGoStraight && (headingDelta <= strightThreshold || headingDelta >= twoPi - strightThreshold)
  }

  def canTurnLeft: Boolean = false
  def canTurnLeft(headingDelta: Angle): Boolean = {
    canTurnLeft && headingDelta > strightThreshold && headingDelta <= leftTurnMaxThreshold
  }

  def canTurnRight: Boolean = false
  def canTurnRight(headingDelta: Angle): Boolean = {
    canTurnRight && headingDelta >= rightTurnMaxThreshold && headingDelta < twoPi - strightThreshold
  }
}
object TurningAllowance {

  case object AnyDirection extends TurningAllowance {
    override def canGoStraight: Boolean = true
    override def canTurnLeft: Boolean = true
    override def canTurnRight: Boolean = true
  }

  case object TurnLeftOnly extends TurningAllowance {
    override def canTurnLeft: Boolean = true
  }

  case object TurnRightOnly extends TurningAllowance {
    override def canTurnRight: Boolean = true
  }

  case object GoStraightOnly extends TurningAllowance {
    override def canGoStraight: Boolean = true
  }

  case object MustTurnAnyDirection extends TurningAllowance {
    override def canTurnLeft: Boolean = true
    override def canTurnRight: Boolean = true
  }

  case object GoStraightOrTurnLeft extends TurningAllowance {
    override def canGoStraight: Boolean = true
    override def canTurnLeft: Boolean = true
  }

  case object GoStraightOrTurnRight extends TurningAllowance {
    override def canTurnRight: Boolean = true
    override def canGoStraight: Boolean = true
  }
}
