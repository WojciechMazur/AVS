package pl.edu.agh.wmazur.avs

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import pl.edu.agh.wmazur.avs.protocol.SimulationProtocol

trait Agent[Protocol <: SimulationProtocol] extends AbstractBehavior[Protocol] {
  def context: ActorContext[Protocol]

  protected val initialBehaviour: Behavior[Protocol]

  final override def onMessage(msg: Protocol): Behavior[Protocol] = {
    context.self ! msg
    initialBehaviour
  }
}
