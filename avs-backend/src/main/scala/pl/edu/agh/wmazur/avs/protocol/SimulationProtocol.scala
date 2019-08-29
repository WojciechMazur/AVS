package pl.edu.agh.wmazur.avs.protocol

import akka.actor.typed.ActorRef
import pl.edu.agh.wmazur.avs.model.entity.intersection.reservation.ReservationArray.Timestamp

import scala.concurrent.duration.FiniteDuration

trait SimulationProtocol
object SimulationProtocol {

  trait Tick extends SimulationProtocol
  object Tick {
    case class Default(currentTime: Timestamp,
                       timeDelta: FiniteDuration,
                       step: Long)
        extends Tick
  }

  trait SimulationUpdate extends SimulationProtocol
  object SimulationUpdate {
    case object Empty extends SimulationUpdate
    case class Failed(throwable: Throwable) extends SimulationUpdate

    def empty: SimulationUpdate = Empty
    def failed(reason: Throwable): SimulationUpdate = Failed(reason)
  }

  trait Ack extends SimulationProtocol
//  case object Ack extends SimulationProtocol

  trait Done extends SimulationProtocol
//  case object Done extends SimulationProtocol
}
