package pl.edu.agh.wmazur.avs.http.management

import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior, Terminated}
import pl.edu.agh.wmazur.avs.Services
import pl.edu.agh.wmazur.avs.http.flows.SimulationStateDeltaParser.LastStateProvider

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
    context.system.receptionist ! Receptionist.register(
      Services.webSocketManager,
      context.self)
    val ref = context.spawn(LastStateProvider.persist(), "lastStateProvider")
    lastStateProvider = ref
    supervise()
  }

  private def supervise(
      connections: Connections = Map.empty): Behavior[Protocol] =
    Behaviors
      .receivePartial[Protocol] {
        case (context, message) =>
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
            case _ => Behaviors.same
          }
      }
      .receiveSignal {
        case (ctx, Terminated(ref)) =>
          ctx.log.debug("Terminated connecion {}", ref.path)
          Behaviors.same
      }

}
