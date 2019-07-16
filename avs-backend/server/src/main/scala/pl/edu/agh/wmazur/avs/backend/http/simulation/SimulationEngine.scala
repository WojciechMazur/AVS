package pl.edu.agh.wmazur.avs.backend.http.simulation

import akka.NotUsed
import akka.stream.SourceShape
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Source, ZipWith}
import com.softwaremill.quicklens._
import pl.edu.agh.wmazur.avs.backend.http.flows.SimulationEngineProxy
import pl.edu.agh.wmazur.avs.backend.http.simulation.stage.{
  DriversExecutor,
  VehiclesCollector,
  VehiclesSpawner
}
import pl.edu.agh.wmazur.avs.model.state.SimulationState
import protobuf.pl.edu.agh.wmazur.avs.model.StateModificationEvent

import scala.collection.mutable.ListBuffer

object SimulationEngine {

  val stateModificationEvents: Source[StateModificationEvent, NotUsed] =
    SimulationEngineProxy.mergedCommandsSource

  val stateProcessingFlow: Flow[SimulationState, SimulationState, NotUsed] =
    Flow[SimulationState]
      .via(VehiclesSpawner.flow)
      .via(DriversExecutor.flow)
      //
      .via(VehiclesCollector.flow)

  val recoverOrInitState: Source[SimulationState, NotUsed] =
    Source.single(SimulationState.init)

  def stateUpdatedByExternalChanges(
      state: SimulationState,
      events: Iterable[StateModificationEvent]): SimulationState = state

  private val groupedStateExternalChanges
    : Source[List[StateModificationEvent], NotUsed] = stateModificationEvents
    .batch(100, ListBuffer.apply(_))(_ += _)
    .prepend(Source.single(ListBuffer.empty[StateModificationEvent]))
    .expand { elem =>
      Iterator.single(elem.toList) ++ Iterator.continually(List.empty)
    }

  val simulationStateSource: Source[SimulationState, NotUsed] =
    Source.fromGraph {
      GraphDSL.create() { implicit builder =>
        import GraphDSL.Implicits._
        val simulationProcessor =
          builder.add(stateProcessingFlow.prepend(recoverOrInitState))

        val stateZipper = builder.add(
          ZipWith[TickSource.TickDelta,
                  List[StateModificationEvent],
                  SimulationState,
                  SimulationState] {
            case (delta, events, simulationState) =>
              stateUpdatedByExternalChanges(simulationState, events)
                .modify(_.tickDelta)
                .setTo(delta)
                .modify(_.currentTime)
                .using(_ + delta.toMillis.toInt)
          })

        val stateBroadcast = builder.add(Broadcast[SimulationState](2))

        stateZipper.in0 <~ TickSource.source
        stateZipper.in1 <~ groupedStateExternalChanges
        stateZipper.in2 <~ stateBroadcast

        stateZipper.out ~> simulationProcessor ~> stateBroadcast

        SourceShape(stateBroadcast.outlet)
      }
    }

}
