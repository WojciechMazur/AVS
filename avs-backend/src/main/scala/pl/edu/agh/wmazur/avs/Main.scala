package pl.edu.agh.wmazur.avs

import akka.actor
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.adapter._
import akka.actor.typed.{ActorSystem, Behavior, Terminated}
import akka.http.scaladsl.Http
import akka.stream.typed.scaladsl.ActorMaterializer
import pl.edu.agh.wmazur.avs.http.management.WebsocketManager
import pl.edu.agh.wmazur.avs.http.routes.WebsocketRoute
import pl.edu.agh.wmazur.avs.simulation.SimulationManager

import scala.concurrent.ExecutionContextExecutor

object Main extends App {

  implicit lazy val systemGuardian: Behavior[akka.NotUsed] = Behaviors.setup {
    ctx =>
      val wsManager = ctx.spawn(WebsocketManager.init, "ws-manager")
      val simulationManager = ctx.spawn(SimulationManager(), "sim-manager")

      Http().bindAndHandle(new WebsocketRoute(wsManager).route,
                           "localhost",
                           8081)
      Behaviors.receiveSignal {
        case (_, Terminated(_)) =>
          Behaviors.stopped
      }
  }

  implicit lazy val system: ActorSystem[Nothing] =
    ActorSystem(systemGuardian, "avs")
  implicit lazy val untypedSystem: actor.ActorSystem = system.toUntyped
  implicit lazy val materializer: ActorMaterializer = ActorMaterializer()

  implicit val executionContext: ExecutionContextExecutor =
    system.executionContext

  system.whenTerminated.map { _ =>
    println("System terminated")
  }

}

/**
  * TODO
  * 2. Sprawdzić rzucanie scala.runtime.NonLocalReturnControl w bibliotece scala-graph.
  * 4. Pojazdy nie próbują ponowanie zarezerwować przejazdu?
  * 5. LaneCollector nie działa.
  */
