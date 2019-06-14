package pl.edu.agh.wmazur.avs.backend.http.codec

import java.util.concurrent.atomic.AtomicInteger

import akka.stream.scaladsl.Flow
import protobuf.pl.agh.edu.agh.wmazur.avs.model.ConnectivityEvents.EventType
import protobuf.pl.agh.edu.agh.wmazur.avs.model.{ConnectivityEvents, Envelope}
import protobuf.pl.agh.edu.agh.wmazur.avs.model.Envelope.Message
import pl.edu.agh.wmazur.avs.backend.http.management.WebsocketManager
import pl.edu.agh.wmazur.avs.backend.http.management.WebsocketManager.{
  ClientJoined,
  ClientLeaved
}

object WebsocketMessageEncoder
    extends MessageEncoder[WebsocketManager.Protocol,
                           Envelope.Message.ConnectivityEvents] {
  private val activeClients = new AtomicInteger(0)

  override protected def decodingFunction
    : PartialFunction[WebsocketManager.Protocol, ConnectivityEvents] = {
    case ClientJoined(connectionId, _) =>
      ConnectivityEvents(EventType.ClientJoined,
                         connectionId,
                         activeClients.incrementAndGet())
    case ClientLeaved(connectionId) =>
      ConnectivityEvents(EventType.ClientLeaved,
                         connectionId,
                         activeClients.decrementAndGet())
  }

  override protected val toWrapper = Message.ConnectivityEvents.apply
}
