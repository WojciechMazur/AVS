package pl.edu.agh.wmazur.avs.backend.http.management

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import pl.edu.agh.wmazur.avs.backend.http.flows.SimulationStateDeltaParser.LastStateProvider

object WebsocketManager {
  type ConnectionId = String
  type Connections = Map[ConnectionId, ActorRef[WebsocketManager.Protocol]]

  sealed trait Protocol
  case class ClientJoined(connectionId: ConnectionId,
                          actorRef: ActorRef[WebsocketManager.Protocol])
      extends Protocol
  case class ClientLeaved(connectionId: ConnectionId) extends Protocol
  case class Dispatch(message: WebsocketManager.Protocol) extends Protocol
  case class StateChanged() extends Protocol
  case class WebsocketFailure(err: Throwable) extends Protocol

  var lastStateProvider: ActorRef[LastStateProvider.Protocol] = _

  val init: Behavior[Protocol] = Behaviors.setup { context =>
    val ref = context.spawn(LastStateProvider.persist(), "lastStateProvider")
    lastStateProvider = ref
    supervise()
  }

  private def supervise(
      connections: Connections = Map.empty): Behaviors.Receive[Protocol] =
    Behaviors.receive[Protocol] { (context, message) =>
      message match {
        case ClientJoined(connectionId, actorRef) =>
          context.watch(actorRef)
          context.log.debug(s"Connection $connectionId established")
          supervise(connections + (connectionId -> actorRef))

        case ClientLeaved(connectionId) =>
          val connectionManager = connections(connectionId)
          context.unwatch(connectionManager)
          context.log.debug(s"Connection $connectionId terminated")
          supervise(connections - connectionId)

        case Dispatch(msg) =>
          connections.foreach { case (_, ref) => ref ! msg }
          Behaviors.same
      }
    }

}
