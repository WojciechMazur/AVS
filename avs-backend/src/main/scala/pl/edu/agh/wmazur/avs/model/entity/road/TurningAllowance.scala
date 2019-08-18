package pl.edu.agh.wmazur.avs.model.entity.road

sealed trait TurningAllowance {
  def canGoStraight: Boolean = false
  def canTurnLeft: Boolean = false
  def canTurnRight: Boolean = false
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
