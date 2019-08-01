package pl.edu.agh.wmazur.avs.http.flows

import akka.NotUsed
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import akka.stream.scaladsl.{Flow, Sink}
import akka.stream.typed.scaladsl.ActorMaterializer
import akka.util.ByteString
import protobuf.pl.edu.agh.wmazur.avs.model.Envelope

import scala.concurrent.duration._

case class ProtobufCodec(
    innerFlow: Flow[Envelope.Message, Envelope.Message, Any])(
    implicit actorMaterializer: ActorMaterializer) {
  private val messageDecoder: Flow[Message, Envelope.Message, NotUsed] = {
    val textMessagesSink: Sink[Message, NotUsed] = Flow[Message]
      .collectType[TextMessage]
      .mapAsync(1)(_.toStrict(5.seconds))
      .map(_.text)
      .map(msg => println(s"Unsupported text message: $msg"))
      .to(Sink.ignore)

    Flow[Message]
      .divertTo(textMessagesSink, _.isText)
      .collectType[BinaryMessage]
      .mapAsync(1)(_.toStrict(5.seconds))
      .map(_.data)
      .map(bytes => Envelope.parseFrom(bytes.toArray))
      .map(_.message)
  }

  private val messageEncoder: Flow[Envelope.Message, Message, NotUsed] =
    Flow[Envelope.Message]
      .map(msg => Envelope(msg).toByteArray)
      .map(ByteString.fromArray)
      .map(BinaryMessage.apply)

  val flow: Flow[Message, Message, NotUsed] =
    Flow[Message]
      .via(messageDecoder)
      .via(innerFlow)
      .via(messageEncoder)
}
