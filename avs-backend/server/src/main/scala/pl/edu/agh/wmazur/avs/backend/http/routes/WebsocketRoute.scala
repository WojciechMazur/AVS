package pl.edu.agh.wmazur.avs.backend.http.routes

import akka.NotUsed
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import akka.http.scaladsl.server.Directives.{handleWebSocketMessages, path}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.{ActorMaterializer, FlowShape, OverflowStrategy}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Merge, Sink}
import akka.stream.typed.scaladsl.{ActorSink, ActorSource}
import akka.util.ByteString
import pl.agh.edu.agh.wmazur.avs.model.protobuf.ConnectivityEvents.EventType
import pl.agh.edu.agh.wmazur.avs.model.protobuf.Envelope.Message.Acknowledged
import pl.agh.edu.agh.wmazur.avs.model.protobuf.{
  ConnectivityEvents,
  Envelope,
  SimulationEvent
}
import pl.edu.agh.wmazur.avs.backend.http.codec.WebsocketEventsEncoder
import pl.edu.agh.wmazur.avs.backend.http.management.WebsocketManager
import pl.edu.agh.wmazur.avs.backend.http.management.WebsocketManager.{
  ClientJoined,
  ClientLeaved,
  ConnectionId,
  Dispatch,
  StateChanged,
  WebsocketFailure
}
import scalapb.GeneratedMessage
import scalapb.lenses.Updatable

import scala.concurrent.Await
import scala.concurrent.duration._

class WebsocketRoute(wsManagerRef: ActorRef[WebsocketManager.Protocol])(
    implicit actorSystem: ActorSystem[_],
    materializer: ActorMaterializer) {

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

  private val websocketEventsSource =
    ActorSource.actorRef[WebsocketManager.Protocol](
      completionMatcher = PartialFunction.empty[WebsocketManager.Protocol, Unit],
      failureMatcher = {
        case WebsocketFailure(err) => err
      },
      bufferSize = 16,
      overflowStrategy = OverflowStrategy.fail
    )

  private def messegingFlow(connectionId: ConnectionId) = Flow.fromGraph {
    GraphDSL.create(websocketEventsSource) {
      implicit builder => websocketEvents =>
        import GraphDSL.Implicits._
        import WebsocketRoute._
        //Default coders, Envelope.Message <-> Message(Binary)
        val msgDecoder = builder.add(messageDecoder)
        val msgEncoder = builder.add(messageEncoder)

        // Message collectors
        val entityCommands = builder.add(collectEntityCommands)
        val connectivityEvents = builder.add(collectConnectivityEvents)

        // Events created at opening / closing connection
        val connectionOpenedMessages =
          builder.materializedValue.map(ref => ClientJoined(connectionId, ref))
        val websocketManagerSink: Sink[WebsocketManager.Protocol, NotUsed] =
          ActorSink.actorRef(wsManagerRef,
                             ClientLeaved(connectionId),
                             err => WebsocketFailure(err))

        // Concrete events coders
        val wsEventsEncoder = builder.add(WebsocketEventsEncoder.flow)
        val wsEventsDecoder = builder.add {
          Flow[ConnectivityEvents]
            .filter(_.`type`.isUnrecognized)
            .map(e => ClientLeaved(e.targetId))
        }
        // Utility hubs
        val connectivityEventsMerge =
          builder.add(Merge[WebsocketManager.Protocol](2))
        val dispatchDecodedMsg = builder.add(Broadcast[Envelope.Message](2))

        //Flow blueprint
        msgDecoder ~> dispatchDecodedMsg
        dispatchDecodedMsg ~> entityCommands ~> Sink.ignore
        dispatchDecodedMsg ~> connectivityEvents ~> wsEventsDecoder ~> connectivityEventsMerge
        connectionOpenedMessages ~> connectivityEventsMerge
        connectivityEventsMerge ~> websocketManagerSink

        //Events sent from WebsocketManager Actor
        websocketEvents ~> wsEventsEncoder ~> msgEncoder

        FlowShape(msgDecoder.in, msgEncoder.out)
    }
  }
  val route: Route = path("avs" / JavaUUID) { connectionId =>
    handleWebSocketMessages(messegingFlow(connectionId.toString))
  }

}

object WebsocketRoute {
  def apply(connectionId: ConnectionId)(
      implicit actorSystem: ActorSystem[_],
      materializer: ActorMaterializer): WebsocketRoute = {
    val wsManagerRef = Await.result(
      actorSystem.systemActorOf(WebsocketManager.supervise(), "wsManager")(
        5.seconds),
      Duration.Inf
    )
    new WebsocketRoute(wsManagerRef)
  }

  lazy val eventCollectors: Seq[Flow[Envelope.Message, _, NotUsed]] =
    Seq(collectConnectivityEvents, collectEntityCommands)

  val collectConnectivityEvents
    : Flow[Envelope.Message, ConnectivityEvents, NotUsed] =
    Flow[Envelope.Message]
      .collect {
        case Envelope.Message.ConnectivityEvents(event) => event
      }

  val collectEntityCommands: Flow[Envelope.Message, SimulationEvent, NotUsed] =
    Flow[Envelope.Message]
      .collect {
        case Envelope.Message.SimulationEvent(event) => event
      }
}
