package pl.edu.agh.wmazur.avs.model.entity.intersection.policy

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import pl.edu.agh.wmazur.avs.model.entity.intersection.AutonomousIntersectionManager.Protocol.FetchDrivers
import pl.edu.agh.wmazur.avs.model.entity.intersection.{
  AutonomousIntersectionManager,
  IntersectionManager
}
import pl.edu.agh.wmazur.avs.model.entity.utils.SpatialUtils
import pl.edu.agh.wmazur.avs.simulation.stage.SimulationStateGatherer

trait IntersectionConnectivity {
  self: AutonomousIntersectionManager =>
  import IntersectionManager._

  lazy val driversFetcherManager: ActorRef[DriversFetcherAgent.Protocol] =
    self.context.spawn(
      DriversFetcherAgent.init(
        intersectionPosition = intersection.position,
        intersectionGeometry =
          SpatialUtils.shapeFactory.getGeometryFrom(intersection.area),
        entryPoints = intersection.entryPoints,
        exitPoints = intersection.exitPoints,
        transmitionDistance =
          AutonomousIntersectionManager.maxTransmitionDistance,
        intersectionManager = context.self
      ),
      "drivers-fetcher"
    )

  lazy val basicConnectivity: Behavior[IntersectionManager.Protocol] =
    Behaviors.receiveMessagePartial {
      case GetDetailedReadings(replyTo) =>
        replyTo ! SimulationStateGatherer.IntersectionDetailedReading(
          context.self,
          intersection)

        Behaviors.same

      case FetchDrivers =>
        val roadManagers = roads.map(_.managerRef)

        driversFetcherManager ! DriversFetcherAgent.FetchDrivers(roadManagers)

        Behaviors.same
    }
}
