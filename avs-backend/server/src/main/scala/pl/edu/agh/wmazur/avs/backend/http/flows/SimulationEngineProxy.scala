package pl.edu.agh.wmazur.avs.backend.http.flows

import akka.{Done, NotUsed}
import akka.stream.scaladsl.{BroadcastHub, Keep, MergeHub, Sink, Source}
import akka.stream.typed.scaladsl.ActorMaterializer
import pl.agh.edu.agh.wmazur.avs.model.SimulationState
import pl.edu.agh.wmazur.avs.backend.http.WebSocketServer
import pl.edu.agh.wmazur.avs.backend.http.simulation.SimulationEngine
import protobuf.pl.agh.edu.agh.wmazur.avs.model.StateModificationEvent

import scala.concurrent.Future

object SimulationEngineProxy {
  private implicit lazy val actorMaterialier: ActorMaterializer =
    WebSocketServer.materializer

  private val sinkAsPublisher =
    Sink.asPublisher[StateModificationEvent](fanout = true)

  val (clientCommandsSink, mergedCommandsSource) = MergeHub
    .source[StateModificationEvent](32)
    .toMat(sinkAsPublisher)(Keep.both)
    .mapMaterializedValue {
      case (sink, publisher) => (sink, Source.fromPublisher(publisher))
    }
    .run()

  val simulationStatePublisher: Source[SimulationState, NotUsed] =
    SimulationEngine.simulationStateSource
      .toMat(BroadcastHub.sink(64))(Keep.right)
      .run()

  private val simulationDrainer: Future[Done] =
    simulationStatePublisher.runWith(Sink.ignore)

//  private val simualtionLogger = simulationStatePublisher.runForeach(println)
}
