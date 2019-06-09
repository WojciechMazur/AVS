package pl.edu.agh.wmazur.avs.backend.http.codec

import java.util.concurrent.atomic.AtomicInteger

import akka.stream.scaladsl.Flow
import pl.agh.edu.agh.wmazur.avs.model.protobuf.ConnectivityEvents.EventType
import pl.agh.edu.agh.wmazur.avs.model.protobuf.{ConnectivityEvents, Envelope}
import pl.agh.edu.agh.wmazur.avs.model.protobuf.Envelope.Message
import pl.edu.agh.wmazur.avs.backend.http.management.WebsocketManager
import pl.edu.agh.wmazur.avs.backend.http.management.WebsocketManager.{
  ClientJoined,
  ClientLeaved
}

object WebsocketEventsEncoder
    extends EventsEncoder[WebsocketManager.Protocol,
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

  override protected def toWrapper(
      out: WebsocketEventsEncoder.Out): Message.ConnectivityEvents =
    Message.ConnectivityEvents(out)
}
