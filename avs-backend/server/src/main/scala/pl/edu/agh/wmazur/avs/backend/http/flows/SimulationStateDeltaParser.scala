package pl.edu.agh.wmazur.avs.backend.http.flows

import akka.NotUsed
import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.Behaviors
import akka.stream.FlowShape
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Source, Zip}
import akka.stream.typed.scaladsl.ActorSink
import pl.edu.agh.wmazur.avs.backend.http.flows.SimulationStateDeltaParser.LastStateProvider.UpdateState
import pl.edu.agh.wmazur.avs.backend.http.management.WebsocketManager
import pl.edu.agh.wmazur.avs.model.state.{
  SimulationState,
  SimulationStateDelta,
  SimulationStateUpdate
}

object SimulationStateDeltaParser {
  val flow: Flow[SimulationState, SimulationStateDelta, NotUsed] =
    Flow.fromGraph {
      GraphDSL.create() { implicit builder =>
        import GraphDSL.Implicits._
        val stateZipper = builder.add(Zip[SimulationState, SimulationState])
        val stateBroadcast = builder.add(Broadcast[SimulationState](3))
        val lastState = builder.add {
          Flow[SimulationState]
            .prepend {
              Source.single(SimulationState.init)
            }
        }

        val deltaCalculator = builder.add {
          Flow[(SimulationState, SimulationState)]
            .map {
              case (previous, current) =>
                SimulationStateUpdate(previous, current)
            }
        }

        stateZipper.in0 <~ lastState <~ stateBroadcast
        stateZipper.in1 <~ stateBroadcast
        stateZipper.out ~> deltaCalculator

        stateBroadcast ~> Flow[SimulationState].map(UpdateState) ~> ActorSink
          .actorRef[LastStateProvider.Protocol](
            WebsocketManager.lastStateProvider,
            LastStateProvider.Finished,
            err => LastStateProvider.Failure(err)
          )

        FlowShape(stateBroadcast.in, deltaCalculator.out)
      }
    }

  object LastStateProvider {
    sealed trait Protocol
    case class UpdateState(state: SimulationState) extends Protocol
    case class GetState(replyTo: ActorRef[Protocol]) extends Protocol
    case class StateResponse(state: SimulationState) extends Protocol
    case class Failure(throwable: Throwable) extends Protocol
    case object Finished extends Protocol

    def persist(state: SimulationState = SimulationState.init)
      : Behaviors.Receive[Protocol] =
      Behaviors.receiveMessagePartial[Protocol] {
        case UpdateState(updatedState) =>
          persist(updatedState)
        case GetState(ref) =>
          ref ! StateResponse(state)
          Behaviors.same
        case Failure(err) =>
          println(err)
          Behaviors.same
        case Finished =>
          println("Finished persisting last simulation state")
          Behaviors.same
      }
  }
}
