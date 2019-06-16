package pl.edu.agh.wmazur.avs.backend.http.codec

import akka.NotUsed
import akka.stream.scaladsl.Flow
import protobuf.pl.edu.agh.wmazur.avs.model.Envelope

import scala.language.implicitConversions

trait MessageEncoder[-In, OutWrapper <: Envelope.Message] {
  type Out = OutWrapper#ValueType

  protected def decodingFunction: PartialFunction[In, Out]
  protected val toWrapper: Out => OutWrapper

  final implicit def outToEnvelopeMessage(out: Out): Envelope.Message =
    toWrapper(out)

  final val flow: Flow[In, Envelope.Message, NotUsed] =
    Flow[In]
      .collect(decodingFunction)
      .map(outToEnvelopeMessage)
}
