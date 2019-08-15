package pl.edu.agh.wmazur.avs.model.entity.intersection.extension

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.intersection.AutonomousIntersectionManager.Protocol.{
  FetchDrivers,
  Tick
}
import pl.edu.agh.wmazur.avs.model.entity.intersection.workers.DriversFetcherAgent
import pl.edu.agh.wmazur.avs.model.entity.intersection.{
  AutonomousIntersectionManager,
  IntersectionManager
}
import pl.edu.agh.wmazur.avs.simulation.stage.SimulationStateGatherer

import scala.concurrent.duration._

trait IntersectionConnectivity {
  self: AutonomousIntersectionManager =>
  import IntersectionManager._

  timers.startPeriodicTimer(FetchDrivers,
                            FetchDrivers,
                            IntersectionConnectivity.transmitionInterval)

  lazy val basicConnectivity: Behavior[IntersectionManager.Protocol] =
    Behaviors
      .receiveMessagePartial[IntersectionManager.Protocol] {
        case GetDetailedReadings(replyTo) =>
          replyTo ! SimulationStateGatherer.IntersectionDetailedReading(
            context.self,
            intersection)

          Behaviors.same

        case FetchDrivers =>
          val roadManagers = roads.map(_.managerRef)
          workers.driversFetcher ! DriversFetcherAgent.FetchDrivers(
            roadManagers)
          Behaviors.same
        case Tick(time) =>
          this.currentTime = time
          mainReservationManager.clean(time)
          Behaviors.same
      }
      .narrow
}

object IntersectionConnectivity {
  val transmitionInterval: FiniteDuration = 1.seconds
  val maxTransmitionDistance: Dimension = 200.asMeters

}
