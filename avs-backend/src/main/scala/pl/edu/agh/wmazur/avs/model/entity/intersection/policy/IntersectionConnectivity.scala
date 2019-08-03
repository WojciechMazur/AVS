package pl.edu.agh.wmazur.avs.model.entity.intersection.policy

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior, Terminated}
import org.locationtech.jts.geom.Geometry
import org.locationtech.spatial4j.shape.{Point, Shape}
import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.intersection.AutonomousIntersectionManager.Protocol.FetchDrivers
import pl.edu.agh.wmazur.avs.model.entity.intersection.policy.IntersectionConnectivity.DriversFetcher
import pl.edu.agh.wmazur.avs.model.entity.intersection.{
  AutonomousIntersectionManager,
  IntersectionManager
}
import pl.edu.agh.wmazur.avs.model.entity.road.{Lane, RoadManager}
import pl.edu.agh.wmazur.avs.model.entity.utils.SpatialUtils
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.Angle
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.{
  AutonomousDriver,
  VehicleDriver
}
import pl.edu.agh.wmazur.avs.simulation.stage.SimulationStateGatherer

trait IntersectionConnectivity {
  self: AutonomousIntersectionManager =>
  import IntersectionManager._

  lazy val driversFetcherManager: ActorRef[DriversFetcher.Protocol] =
    self.context.spawn(
      DriversFetcher.init(
        intersection.position,
        SpatialUtils.shapeFactory.getGeometryFrom(intersection.area),
        AutonomousIntersectionManager.maxTransmitionDistance,
        context.self
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

        driversFetcherManager ! DriversFetcher.FetchDrivers(roadManagers)

        Behaviors.same
    }
}

object IntersectionConnectivity {

  object DriversFetcher {
    import SpatialUtils._
    private case class Context(
        context: ActorContext[Protocol],
        transmitionArea: Shape,
        intersectionCenter: Point,
        intersectionGeometry: Geometry,
        intersectionManagerRef: ActorRef[IntersectionManager.Protocol],
        lanesOccupationAdapter: ActorRef[RoadManager.LanesOccupation],
        driversPositionAdapter: ActorRef[AutonomousDriver.PositionReading]
    )

    sealed trait Protocol extends IntersectionManager.Protocol
    case class FetchDrivers(
        roadManagers: Iterable[ActorRef[RoadManager.Protocol]])
        extends Protocol
    case class LanesOccupation(
        roadManagerRef: ActorRef[RoadManager.Protocol],
        vehiclesAtLanes: Map[Lane,
                             Set[ActorRef[AutonomousDriver.ExtendedProtocol]]])
        extends Protocol
    case class DriverReading(
        driverRef: ActorRef[AutonomousDriver.ExtendedProtocol],
        position: Point,
        heading: Angle)
        extends Protocol

    def init(intersectionPosition: Point,
             intersectionGeometry: Geometry,
             transmitionDistance: Dimension,
             intersectionManager: ActorRef[IntersectionManager.Protocol])
      : Behavior[Protocol] =
      Behaviors.setup { ctx =>
        val transmitionArea = intersectionPosition.getBuffered(
          transmitionDistance.geoDegrees,
          SpatialUtils.shapeFactory.getSpatialContext)

        val lanesOccupationAdapter =
          ctx.messageAdapter[RoadManager.LanesOccupation] {
            case RoadManager.LanesOccupation(ref, vehiclesAtLanes) =>
              LanesOccupation(ref, vehiclesAtLanes)
          }

        val driversPositionAdapter: ActorRef[AutonomousDriver.PositionReading] =
          ctx.messageAdapter[AutonomousDriver.PositionReading] {
            case AutonomousDriver.PositionReading(ref, position, heading, _) =>
              DriverReading(ref, position, heading)
          }

        idle {
          Context(
            context = ctx,
            transmitionArea = transmitionArea,
            intersectionCenter = intersectionPosition,
            intersectionGeometry = intersectionGeometry,
            intersectionManagerRef = intersectionManager,
            lanesOccupationAdapter = lanesOccupationAdapter,
            driversPositionAdapter = driversPositionAdapter
          )
        }
      }

    def idle(context: Context): Behavior[Protocol] = {
      Behaviors.receiveMessagePartial {
        case FetchDrivers(roadManagers) =>
          roadManagers.foreach(
            _ ! RoadManager.GetLanesOccupation(context.lanesOccupationAdapter))

          waitForDriversAtLanes(context, roadManagers.toSet, Map.empty)
      }
    }

    def waitForDriversAtLanes(
        context: Context,
        awaitingManagers: Set[ActorRef[RoadManager.Protocol]],
        driversAtLanes: Map[Lane,
                            Set[ActorRef[AutonomousDriver.ExtendedProtocol]]])
      : Behavior[Protocol] = {
      if (awaitingManagers.isEmpty) {
        val drivers = driversAtLanes.values.flatten.toSet
        drivers.foreach { ref =>
          ref ! AutonomousDriver.GetPositionReading(
            context.driversPositionAdapter)
          context.context.watch(ref)
        }

        val driversOccupation
          : Map[ActorRef[AutonomousDriver.ExtendedProtocol], Set[Lane]] = {
          for {
            (lane, drivers) <- driversAtLanes
            driver <- drivers
          } yield driver -> lane
        }.groupBy(_._1)
          .mapValues(_.values.toSet)

        waitForDriversReadings(context, drivers, driversOccupation)
      } else {
        Behaviors.receiveMessage {
          case LanesOccupation(roadManagerRef, vehiclesAtLanes) =>
            waitForDriversAtLanes(
              context = context,
              awaitingManagers = awaitingManagers - roadManagerRef,
              driversAtLanes = driversAtLanes ++ vehiclesAtLanes)
        }
      }
    }

    def waitForDriversReadings(
        context: Context,
        awaitingDrivers: Set[ActorRef[AutonomousDriver.ExtendedProtocol]],
        driverLanesOccupation: Map[ActorRef[AutonomousDriver.ExtendedProtocol],
                                   Set[Lane]]): Behavior[Protocol] = {

      if (awaitingDrivers.isEmpty) {
        idle(context)
      } else {
        Behaviors
          .receiveMessage[Protocol] {
            case DriverReading(driverRef, position, heading) =>
              def withinArea: Boolean =
                context.transmitionArea.relate(position).intersects()

              def headingToIntersection: Boolean = {
                val angle = position.angle(context.intersectionCenter) - heading
                angle.abs < Math.PI / 6
              }
              val driverPositionGeometry =
                shapeFactory.getGeometryFrom(position)
              if (withinArea && headingToIntersection) {

                val position = shapeFactory.makeShapeFromGeometry {
                  driverLanesOccupation(driverRef)
                    .map { lane =>
                      val x = context.intersectionGeometry.getBoundary
                        .intersection(shapeFactory.getGeometryFrom(lane.area))
                      x.getCentroid
                    }
                    .minBy(_.distance(driverPositionGeometry))
                }.getCenter

                driverRef
                  .unsafeUpcast[VehicleDriver.Protocol] ! VehicleDriver.Protocol
                  .IntersectionManagerInRange(context.intersectionManagerRef,
                                              position)
              }

              context.context.unwatch(driverRef)
              waitForDriversReadings(context,
                                     awaitingDrivers - driverRef,
                                     driverLanesOccupation)
          }
          .receiveSignal {
            case (_,
                  Terminated(
                    ref: ActorRef[AutonomousDriver.ExtendedProtocol])) =>
              waitForDriversReadings(context,
                                     awaitingDrivers - ref,
                                     driverLanesOccupation)
          }
      }

    }

  }
}
