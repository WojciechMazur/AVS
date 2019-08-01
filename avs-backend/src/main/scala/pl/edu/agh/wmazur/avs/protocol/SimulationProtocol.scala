package pl.edu.agh.wmazur.avs.protocol

import akka.actor.typed.ActorRef

import scala.concurrent.duration.FiniteDuration

trait SimulationProtocol
object SimulationProtocol {
  trait SimulationStep
      extends SimulationProtocol
      with Command[SimulationStep, SimulationProtocol] {
    def timeDelta: FiniteDuration
    def replyTo: ActorRef[SimulationProtocol]
    def step: Int
  }

  object SimulationStep {
    final case class Default(
        timeDelta: FiniteDuration,
        replyTo: ActorRef[SimulationProtocol],
        step: Int
    ) extends SimulationStep

    def apply(timeDelta: FiniteDuration,
              replyTo: ActorRef[SimulationProtocol]): SimulationStep =
      Default(timeDelta, replyTo, steps.next())
    val steps: Iterator[Int] = Iterator.from(0)
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
