package pl.edu.agh.wmazur.avs.simulation.utils

sealed trait BasicDirection
case object BasicDirection {

  case object North extends BasicDirection
  case object South extends BasicDirection
  case object East extends BasicDirection
  case object West extends BasicDirection

}
