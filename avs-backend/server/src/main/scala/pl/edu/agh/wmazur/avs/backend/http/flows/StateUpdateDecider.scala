package pl.edu.agh.wmazur.avs.backend.http.flows

import akka.NotUsed
import akka.actor.Scheduler
import akka.actor.typed.scaladsl.AskPattern._
import akka.stream.FlowShape
import akka.stream.scaladsl.{Flow, GraphDSL, MergePreferred, Source}
import akka.util.Timeout
import pl.agh.edu.agh.wmazur.avs.model.entity.utils.{
  SimulationStateDelta,
  SimulationStateUpdate
}
import pl.edu.agh.wmazur.avs.backend.http.WebSocketServer
import pl.edu.agh.wmazur.avs.backend.http.flows.SimulationStateDeltaParser.LastStateProvider.{
  GetState,
  StateResponse
}
import pl.edu.agh.wmazur.avs.backend.http.management.WebsocketManager
import protobuf.pl.agh.edu.agh.wmazur.avs.model.StateUpdate.UpdateType.{
  Delta,
  Full
}
import protobuf.pl.agh.edu.agh.wmazur.avs.model.{StateRequest, StateUpdate}

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

  val decider
    : (StateRequest, SimulationStateDelta) => Future[SimulationStateUpdate] = {
    case (StateRequest(Full), _) =>
      implicit val timeout: Timeout = 1.second
      import WebSocketServer._
      implicit val scheduler: Scheduler = untypedSystem.scheduler

      (WebsocketManager.lastStateProvider ? GetState)
        .mapTo[StateResponse]
        .map(sr => SimulationStateUpdate(sr.state))
    case (StateRequest(Delta), delta: SimulationStateDelta) =>
      Future.successful(delta)
  }
}
