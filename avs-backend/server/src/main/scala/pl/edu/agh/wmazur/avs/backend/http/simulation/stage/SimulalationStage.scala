package pl.edu.agh.wmazur.avs.backend.http.simulation.stage

import akka.NotUsed
import akka.stream.scaladsl.Flow
import pl.edu.agh.wmazur.avs.model.state.SimulationState

trait SimulalationStage {
  def flow: Flow[SimulationState, SimulationState, NotUsed]
}
