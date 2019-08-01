package pl.edu.agh.wmazur.avs.simulation

import akka.NotUsed
import akka.actor.typed.receptionist.Receptionist
import akka.stream.scaladsl.{Flow, GraphDSL, Keep, Sink, Source, ZipWith}
import akka.stream.typed.scaladsl.{ActorSink, ActorSource}
import akka.stream.{OverflowStrategy, SourceShape}
import pl.edu.agh.wmazur.avs.http.flows.SimulationEngineProxy
import pl.edu.agh.wmazur.avs.model.state.SimulationState
import pl.edu.agh.wmazur.avs.simulation.SimulationManager.Protocol
import pl.edu.agh.wmazur.avs.simulation.SimulationManager.Protocol.{
  SimulationTickContext,
  StateUpdate
}
import pl.edu.agh.wmazur.avs.{Main, Services}
import protobuf.pl.edu.agh.wmazur.avs.model.StateModificationEvent

import scala.collection.mutable.ListBuffer
import scala.concurrent.Await
import scala.concurrent.duration._

object SimulationEngine {
  val (stateUpdateRef, publisher) = {
    ActorSource
      .actorRef[StateUpdate](PartialFunction.empty,
                             PartialFunction.empty,
                             1,
                             OverflowStrategy.fail)
      .toMat(Sink.asPublisher(true))(Keep.both)
      .run()(Main.materializer)
  }

  private val simulationStateUpdate = Source
    .fromPublisher(publisher)

  val stateModificationEvents: Source[StateModificationEvent, NotUsed] =
    SimulationEngineProxy.mergedCommandsSource

  val stateProcessingFlow
    : Flow[SimulationTickContext, SimulationState, NotUsed] = {
    val sink =
      Sink.setup {
        case (materializer, _) =>
          val sinkFromFuture = ActorSource
            .actorRef[Receptionist.Listing](
              completionMatcher = PartialFunction.empty,
              failureMatcher = PartialFunction.empty,
              bufferSize = 1,
              OverflowStrategy.fail
            )
            .mapMaterializedValue(
              listingReceptor => {
                Main.system.receptionist ! Receptionist
                  .Find(Services.simulationManager, listingReceptor)
                NotUsed
              }
            )
            .map {
              case Services.simulationManager.Listing(listings) => listings
            }
            .take(1)
            .map { refs =>
              ActorSink.actorRefWithAck(
                ref = refs.head,
                onInitMessage = Protocol.Init.apply,
                messageAdapter = Protocol.Tick.apply,
                onFailureMessage = Protocol.Failure.apply,
                onCompleteMessage = Protocol.Complete,
                ackMessage = Protocol.Ack
              )
            }
            .runWith(Sink.head)(materializer)
          Await.result(sinkFromFuture, 10.seconds)
      }

//    val source = Source
//      .queue[Protocol.StateUpdate](bufferSize = 1,
//                                   OverflowStrategy.backpressure)
    val flow = Flow.fromSinkAndSourceCoupled(sink, simulationStateUpdate)

    Flow[SimulationTickContext]
      .via(flow)
      .map(_.state)
  }

  def stateUpdatesCommandsTranslator(
      events: Iterable[StateModificationEvent]): List[StateUpdateCommand] = Nil

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
          builder.add(stateProcessingFlow)

        val stateZipper = builder.add(
          ZipWith[TickSource.TickDelta,
                  List[StateModificationEvent],
                  StateUpdate,
                  SimulationTickContext] {
            case (delta, events, _) =>
              SimulationTickContext(
                delta,
                stateUpdatesCommandsTranslator(events)
              )
          })

        stateZipper.in0 <~ TickSource.source
        stateZipper.in1 <~ groupedStateExternalChanges
        stateZipper.in2 <~ simulationStateUpdate

        stateZipper.out ~> simulationProcessor

        SourceShape(simulationProcessor.out)
      }
    }

}
