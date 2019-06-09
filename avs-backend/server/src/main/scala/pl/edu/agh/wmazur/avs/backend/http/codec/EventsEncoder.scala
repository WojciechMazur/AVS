package pl.edu.agh.wmazur.avs.backend.http.codec

import akka.NotUsed
import akka.stream.scaladsl.Flow
import pl.agh.edu.agh.wmazur.avs.model.protobuf.Envelope

import scala.language.implicitConversions

trait EventsEncoder[-In, OutWrapper <: Envelope.Message] {
  type Out = OutWrapper#ValueType

  protected def decodingFunction: PartialFunction[In, Out]
  protected def toWrapper(out: Out): OutWrapper

  final implicit def outToEnvelopeMessage(out: Out): Envelope.Message =
    toWrapper(out)

  final def flow: Flow[In, Envelope.Message, NotUsed] =
    Flow[In]
      .collect(decodingFunction)
      .map(outToEnvelopeMessage)
}
