package pl.edu.agh.wmazur.avs.backend.http

import akka.actor
import akka.actor.typed._
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.adapter._
import akka.http.scaladsl.Http
import akka.stream.typed.scaladsl.ActorMaterializer
import pl.edu.agh.wmazur.avs.backend.http.flows.SimulationEngineProxy
import pl.edu.agh.wmazur.avs.backend.http.management.WebsocketManager
import pl.edu.agh.wmazur.avs.backend.http.routes.WebsocketRoute
import pl.edu.agh.wmazur.avs.backend.http.simulation.SimulationEngine

import scala.concurrent.ExecutionContextExecutor

object WebSocketServer extends App {

  implicit val systemGuardian: Behavior[akka.NotUsed] = Behaviors.setup { ctx =>
    val manager = ctx.spawn(WebsocketManager.init, "ws-manager")
    Http().bindAndHandle(new WebsocketRoute(manager).route, "localhost", 8081)

    Behaviors.receiveSignal {
      case (_, Terminated(_)) =>
        Behaviors.stopped
    }
  }

  implicit val system: ActorSystem[akka.NotUsed] =
    ActorSystem(systemGuardian, "avs")
  implicit val untypedSystem: actor.ActorSystem = system.toUntyped
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit val executionContext: ExecutionContextExecutor =
    system.executionContext

  system.whenTerminated.map { _ =>
    println("System terminated")
  }
}
