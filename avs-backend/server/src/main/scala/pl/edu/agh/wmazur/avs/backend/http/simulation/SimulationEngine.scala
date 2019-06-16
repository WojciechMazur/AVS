package pl.edu.agh.wmazur.avs.backend.http.simulation

import akka.NotUsed
import akka.stream.SourceShape
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Source, ZipWith}
import com.softwaremill.quicklens._
import pl.edu.agh.wmazur.avs.model.entity.vehicle.DefaultVehicle
import pl.edu.agh.wmazur.avs.backend.http.flows.SimulationEngineProxy
import pl.edu.agh.wmazur.avs.model.state.SimulationState
import protobuf.pl.edu.agh.wmazur.avs.model.StateModificationEvent

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._

object SimulationEngine {

  val stateModificationEvents: Source[StateModificationEvent, NotUsed] =
    SimulationEngineProxy.mergedCommandsSource

  val stateProcessingFlow: Flow[SimulationState, SimulationState, NotUsed] =
    Flow[SimulationState]
      .map { state =>
        val cars = state.vehicles.collect {
          case (id, v: DefaultVehicle) =>
            val deltaPos = v.speed * state.tickDelta.toUnit(SECONDS)
            val updated = v
              .modify(_.position)
              .using(
                pos =>
                  pos
                    .withX(pos.x + deltaPos.toFloat)
                    .withZ(pos.z + deltaPos.toFloat))
            id -> updated
        }
        state.copy(vehicles = cars)
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
                .modify(_.totalTicks)
                .using(_ + delta.toMillis)
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
