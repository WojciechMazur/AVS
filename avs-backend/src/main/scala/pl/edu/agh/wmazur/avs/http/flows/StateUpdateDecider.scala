package pl.edu.agh.wmazur.avs.http.flows

import akka.NotUsed
import akka.actor.Scheduler
import akka.actor.typed.scaladsl.AskPattern._
import akka.stream.FlowShape
import akka.stream.scaladsl.{Flow, GraphDSL, MergePreferred, Source}
import akka.util.Timeout
import pl.edu.agh.wmazur.avs.Main
import pl.edu.agh.wmazur.avs.http.flows.SimulationStateDeltaParser.LastStateProvider.{
  GetState,
  StateResponse
}
import pl.edu.agh.wmazur.avs.http.management.WebsocketManager
import pl.edu.agh.wmazur.avs.model.state.{
  SimulationState,
  SimulationStateDelta,
  SimulationStateUpdate
}
import protobuf.pl.edu.agh.wmazur.avs.model.StateUpdate.UpdateType.{Delta, Full}
import protobuf.pl.edu.agh.wmazur.avs.model.{StateRequest, StateUpdate}

import scala.concurrent.Future
import scala.concurrent.duration._

object StateUpdateDecider {
  private val defaultRequestType: StateUpdate.UpdateType = Delta
//  val defaultRequestsSource: Source[StateRequest, NotUsed] = Source.repeat(StateRequest(defaultRequestType))
  val stateRequestsMerger: Flow[StateRequest, StateRequest, NotUsed] =
    Flow
      .fromGraph {
        GraphDSL.create() { implicit builder =>
          import GraphDSL.Implicits._
          val merge = builder.add(MergePreferred[StateRequest](1))

          merge <~ Source.repeat(StateRequest(defaultRequestType))
          FlowShape(merge.preferred, merge.out)
        }
      }
      .prepend(Source.single(StateRequest(Full)))

  val decider: (StateRequest,
                SimulationStateDelta,
                SimulationState) => SimulationStateUpdate = {
    case (StateRequest(Full), _, state) =>
      implicit val timeout: Timeout = 1.second
      implicit val scheduler: Scheduler = Main.untypedSystem.scheduler

//      (WebsocketManager.lastStateProvider ? GetState)
//        .mapTo[StateResponse]
//        .map(sr => SimulationStateUpdate(sr.state))(
//          Main.system.executionContext)
//        .map { msg =>
//          println(msg.roads)
//          msg
//        }(Main.system.executionContext)
      SimulationStateUpdate(state)
    case (StateRequest(Delta), delta: SimulationStateDelta, _) =>
      delta
  }
}
