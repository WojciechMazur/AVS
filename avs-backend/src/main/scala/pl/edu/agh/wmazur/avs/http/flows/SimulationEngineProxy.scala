package pl.edu.agh.wmazur.avs.http.flows

import akka.NotUsed
import akka.stream.scaladsl.{BroadcastHub, Keep, MergeHub, Sink, Source}
import akka.stream.typed.scaladsl.ActorMaterializer
import pl.edu.agh.wmazur.avs.Main
import pl.edu.agh.wmazur.avs.model.state.SimulationState
import pl.edu.agh.wmazur.avs.simulation.SimulationEngine
import protobuf.pl.edu.agh.wmazur.avs.model.StateModificationEvent

object SimulationEngineProxy {
  private implicit lazy val actorMaterialier: ActorMaterializer =
    Main.materializer

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
      .toMat(BroadcastHub.sink(1))(Keep.right)
      .run()

  simulationStatePublisher.runWith(Sink.ignore)

//  private val simualtionLogger = simulationStatePublisher
//    .log("Simulation state logger", identity)
//    .runWith(Sink.ignore)
}
