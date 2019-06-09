import akka.stream.scaladsl.Flow

class SimulationManager {}

object SimulationManager {
  case class SimulationState(entities: Map[Int, Int] = Map.empty)
  def flow = Flow[SimulationState]
}

/*
              |-------|
      |-----> | State |
      |       |-------|
      |           |
      |           |
      |           v
      |   |-----------------|
      |   | Update entities |
      |   |-----------------|
      |         |
      |         |
      |         v
      |   |------------|
      |---| New  State |
          |------------|
                |
                |
                |
                v
          |--------------|
          | Websockets   |
          |   Manager    |
          |--------------|
 */
