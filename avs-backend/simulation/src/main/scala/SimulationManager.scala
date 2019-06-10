import akka.NotUsed
import akka.stream.SourceShape
import akka.stream.scaladsl.{
  Broadcast,
  Flow,
  GraphDSL,
  Merge,
  MergeHub,
  MergePreferred,
  Sink,
  Source,
  Zip
}

trait StateModificationEvent

case class SimulationState(entities: Map[Int, Int] = Map.empty)
object SimulationState {
  def init: SimulationState = SimulationState()
}

class SimulationManager(
    stateModificationEvents: Source[Seq[StateModificationEvent], NotUsed],
    stateProcessingFlow: Flow[SimulationState, SimulationState, NotUsed]) {

  val recoverOrInitState: Source[SimulationState, NotUsed] =
    Source.single(SimulationState.init)

  val stateUpdatedByExternalChanges
    : ((SimulationState, Seq[StateModificationEvent])) => SimulationState = {
    case (state, events) => state
  }
  def flow = Source.fromGraph {
    GraphDSL.create() { implicit builder =>
      import GraphDSL.Implicits._
      Flow.fromSinkAndSource()
      val initialState = builder.add(recoverOrInitState)
      val stateModifications = builder.add(stateModificationEvents)
      val simulationProcessor = builder.add(stateProcessingFlow)

      val simulationStateMerge = builder.add(Merge[SimulationState](2))
      val simulationStateDispatch =
        builder.add(Broadcast[SimulationState](2))

      val stateModificationsOrNil =
        builder.add(MergePreferred[Seq[StateModificationEvent]](1))
      val emptyModificationEvents = builder.add(
        Source.repeat(Seq.empty[StateModificationEvent])
      )
      val zipStateWithModificationEvents =
        builder.add(Zip[SimulationState, Seq[StateModificationEvent]])
      val updateStateByExternalChanges =
        builder.add(Flow.fromFunction(stateUpdatedByExternalChanges))

      initialState ~> simulationStateMerge
      simulationStateDispatch
        .out(0) ~> simulationStateMerge ~> zipStateWithModificationEvents.in0

      stateModifications ~> stateModificationsOrNil.preferred
      emptyModificationEvents ~> stateModificationsOrNil

      stateModificationsOrNil ~> zipStateWithModificationEvents.in1
      zipStateWithModificationEvents.out ~> updateStateByExternalChanges

      updateStateByExternalChanges ~> simulationProcessor ~> simulationStateDispatch

      SourceShape(simulationStateDispatch.out(1))
    }
  }

}
