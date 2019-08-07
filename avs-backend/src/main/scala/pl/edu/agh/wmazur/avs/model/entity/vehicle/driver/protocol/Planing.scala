//package pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.protocol
//
//import akka.actor.typed.Behavior
//import akka.actor.typed.scaladsl.Behaviors
//import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousVehicleDriver
//import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousVehicleDriver.ExtendedProtocol
//import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.protocol.Planing.StartPlaning
//
//trait Planing {
//  self: AutonomousVehicleDriver
//    with PreperingReservation
//    with Driving /** with LaneChanging    */ =>
//
//  val planing = Behaviors.receiveMessagePartial{
//    case StartPlaning =>
//
//      Behaviors.same
//  }
//}
//
//object Planing {
//
//  trait Protocol {
//    self: AutonomousVehicleDriver.type =>
//
//  }
//  case object StartPlaning extends ExtendedProtocol
//}
