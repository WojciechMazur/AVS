package pl.edu.agh.wmazur.avs.simulation

import akka.actor.typed.ActorRef
import pl.edu.agh.wmazur.avs.simulation.stage.{
  DriversMovementStage,
  SimulationStateGatherer,
  VehiclesCollectorStage,
  VehiclesSpawnerStage
}

case class SimulationStageWorkers(
    entityManager: ActorRef[EntityManager.Protocol],
    vehiclesSpawner: ActorRef[VehiclesSpawnerStage.Protocol],
    vehiclesCollector: ActorRef[VehiclesCollectorStage.Protocol],
    stateGatherer: ActorRef[SimulationStateGatherer.Protocol],
    driversMovement: ActorRef[DriversMovementStage.Protocol],
)
