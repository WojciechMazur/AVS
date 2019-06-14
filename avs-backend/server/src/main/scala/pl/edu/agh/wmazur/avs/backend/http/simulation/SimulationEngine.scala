package pl.edu.agh.wmazur.avs.backend.http.simulation

import akka.NotUsed
import akka.stream.SourceShape
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Source, ZipWith}
import com.softwaremill.quicklens._
import pl.agh.edu.agh.wmazur.avs.model.SimulationState
import pl.edu.agh.wmazur.avs.backend.http.flows.SimulationEngineProxy
import protobuf.pl.agh.edu.agh.wmazur.avs.model.StateModificationEvent

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._

object SimulationEngine {

  val stateModificationEvents: Source[StateModificationEvent, NotUsed] =
    SimulationEngineProxy.mergedCommandsSource

  val stateProcessingFlow: Flow[SimulationState, SimulationState, NotUsed] =
    Flow[SimulationState]
//      .throttle(1, 1.seconds)
      .map { state =>
        println(state)
        state
      }

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

  def simulationStateSource: Source[SimulationState, NotUsed] =
    Source.fromGraph {
      GraphDSL.create() { implicit builder =>
        import GraphDSL.Implicits._

        val simulationProcessor =
          builder.add(stateProcessingFlow.prepend(recoverOrInitState))

        val simulationStateDispatch =
          builder.add(Broadcast[SimulationState](2))

        val stateZipper = builder.add(
          ZipWith[TickSource.TickDelta,
                  List[StateModificationEvent],
                  SimulationState,
                  SimulationState] {
            case (delta, events, simulationState) =>
              stateUpdatedByExternalChanges(simulationState, events)
                .modify(_.tickDelta)
                .setTo(delta)
                .modify(_.totalTicks)
                .using(_ + delta.toMillis)
          })

        stateZipper.in0 <~ TickSource.source
        stateZipper.in1 <~ groupedStateExternalChanges
        stateZipper.in2 <~ simulationStateDispatch

        stateZipper.out ~> simulationProcessor ~> simulationStateDispatch

        SourceShape(simulationStateDispatch.outlet)
      }
    }

}
