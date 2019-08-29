package pl.edu.agh.wmazur.avs.http.routes

import akka.NotUsed
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives.{handleWebSocketMessages, path, _}
import akka.http.scaladsl.server.Route
import akka.stream.javadsl.ZipLatestWith
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Merge, Sink, ZipWith}
import akka.stream.typed.scaladsl.{ActorSink, ActorSource}
import akka.stream.{ActorMaterializer, FlowShape, OverflowStrategy}
import com.softwaremill.quicklens._
import pl.edu.agh.wmazur.avs.http.codec.{
  SimulationStateUpdateEncoder,
  WebsocketMessageEncoder
}
import pl.edu.agh.wmazur.avs.http.flows.{
  ProtobufCodec,
  SimulationEngineProxy,
  SimulationStateDeltaParser
}
import pl.edu.agh.wmazur.avs.http.management.WebsocketManager
import pl.edu.agh.wmazur.avs.http.management.WebsocketManager.{
  ClientJoined,
  ClientLeaved,
  ConnectionId,
  WebsocketFailure
}
import pl.edu.agh.wmazur.avs.model.state.{
  SimulationState,
  SimulationStateDelta,
  SimulationStateUpdate
}
import protobuf.pl.edu.agh.wmazur.avs.model.{Envelope, StateRequest}

import scala.concurrent.Await
import scala.concurrent.duration._

class WebsocketRoute(wsManagerRef: ActorRef[WebsocketManager.Protocol])(
    implicit actorSystem: ActorSystem[_],
    materializer: ActorMaterializer) {

  private val websocketEventsSource =
    ActorSource.actorRef[WebsocketManager.Protocol](
      completionMatcher = PartialFunction.empty[WebsocketManager.Protocol, Unit],
      failureMatcher = {
        case WebsocketFailure(err) => err
      },
      bufferSize = 16,
      overflowStrategy = OverflowStrategy.fail
    )

  private def messegingFlow(connectionId: ConnectionId) =
    ProtobufCodec {
      Flow.fromGraph {
        GraphDSL.create(websocketEventsSource) {
          implicit builder => websocketEvents =>
            import GraphDSL.Implicits._
            import WebsocketRoute._
            import pl.edu.agh.wmazur.avs.http.flows.StateUpdateDecider._
            // Events created at opening / closing connection
            val connectionOpenedMessages = builder.materializedValue.map(ref =>
              ClientJoined(connectionId, ref))
            val websocketManagerSink: Sink[WebsocketManager.Protocol, NotUsed] =
              ActorSink.actorRef(wsManagerRef,
                                 ClientLeaved(connectionId),
                                 err => WebsocketFailure(err))

            // Utility hubs
            val connectivityEventsMerge =
              builder.add(Merge[WebsocketManager.Protocol](2))
            val protobuffMessagesMerge = builder.add(Merge[Envelope.Message](2))
            val dispatchDecodedMsg = builder.add(Broadcast[Envelope.Message](3))

            val stateUpdateDecider = builder.add {
              ZipWith[StateRequest,
                      SimulationStateDelta,
                      SimulationState,
                      SimulationStateUpdate](decider)
            }

            val stateDeltaBatcher =
              Flow[SimulationStateDelta].batch(Long.MaxValue, identity) {
                case (acc, delta) =>
                  modify(acc)(_.timeDelta)
                    .using(_ + delta.timeDelta)
                    .modify(_.timestamp)
                    .setTo(delta.timestamp)
                    .modify(_.roads)
                    .using(_.plus(delta.roads))
                    .modify(_.vehicles)
                    .using(_.plus(delta.vehicles))
                    .modify(_.intersections)
                    .using(_.plus(delta.intersections))
              }

            //Flow blueprint
            dispatchDecodedMsg ~> collectStateRequests ~> stateRequestsMerger ~> stateUpdateDecider.in0
            dispatchDecodedMsg ~> collectEntityCommands
            dispatchDecodedMsg ~> collectConnectivityEvents ~> connectivityEventsMerge
            connectionOpenedMessages ~> connectivityEventsMerge
            connectivityEventsMerge ~> websocketManagerSink

            //Events sent from WebsocketManager Actor
            websocketEvents ~> WebsocketMessageEncoder.flow ~> protobuffMessagesMerge

            SimulationEngineProxy.simulationStatePublisher ~> SimulationStateDeltaParser.flow ~> stateDeltaBatcher ~> stateUpdateDecider.in1
            SimulationEngineProxy.simulationStatePublisher ~> stateUpdateDecider.in2
            stateUpdateDecider.out ~> SimulationStateUpdateEncoder.flow ~> protobuffMessagesMerge

            FlowShape(dispatchDecodedMsg.in, protobuffMessagesMerge.out)
        }
      }
    }.flow

  val route: Route = path("avs" / JavaUUID) { connectionId =>
    handleWebSocketMessages(messegingFlow(connectionId.toString))
  }

}

object WebsocketRoute {
  def apply()(implicit actorSystem: ActorSystem[_],
              materializer: ActorMaterializer): WebsocketRoute = {
    val wsManagerRef = Await.result(
      actorSystem.systemActorOf(WebsocketManager.init, "wsManager")(5.seconds),
      Duration.Inf
    )
    new WebsocketRoute(wsManagerRef)
  }

  val collectConnectivityEvents
    : Flow[Envelope.Message, WebsocketManager.Protocol, NotUsed] =
    Flow[Envelope.Message]
      .collect {
        case Envelope.Message.ConnectivityEvents(event) => event
      }
      .filter(_.`type`.isUnrecognized)
      .map(e => ClientLeaved(e.targetId))

  val collectEntityCommands: Sink[Envelope.Message, NotUsed] =
    Flow[Envelope.Message]
      .collect {
        case Envelope.Message.SimulationEvent(event) => event
      }
      .to(SimulationEngineProxy.clientCommandsSink)

  val collectStateRequests: Flow[Envelope.Message, StateRequest, NotUsed] =
    Flow[Envelope.Message]
      .collect {
        case Envelope.Message.StateRequest(v) => v
      }
}
