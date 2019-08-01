package pl.edu.agh.wmazur.avs

import akka.actor.typed.ActorRef
import pl.edu.agh.wmazur.avs.model.entity.utils.IdProvider

package object protocol {
  trait Command[Protocol, ReplyProtocol] {
    self: Protocol =>

    val id: Long = Command.nextId
    def replyTo: ActorRef[ReplyProtocol]
  }

  object Command extends IdProvider[Command[_, _]]

  abstract class Ack[Protocol](commandId: Long) {
    self: Protocol =>
  }
  abstract class Response[Protocol](commandId: Long) {
    self: Protocol =>
  }

}
