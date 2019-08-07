package pl.edu.agh.wmazur.avs

import akka.actor.typed.ActorRef
import pl.edu.agh.wmazur.avs.model.entity.utils.IdProvider

package object protocol {
  trait Request[Protocol, ReplyProtocol] {
    self: Protocol =>

    val id: Long = Request.nextId
    def replyTo: ActorRef[ReplyProtocol]
  }

  object Request extends IdProvider[Request[_, _]]

  abstract class Ack[Protocol](commandId: Long) {
    self: Protocol =>
  }
  abstract class Response[Protocol](commandId: Long) {
    self: Protocol =>
  }

}
