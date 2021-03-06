package pl.edu.agh.wmazur.avs.model.entity.intersection.workers

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior, Terminated}
import org.locationtech.jts.geom
import org.locationtech.jts.geom.{Coordinate, Geometry}
import org.locationtech.jts.geom.prep.{
  PreparedGeometry,
  PreparedGeometryFactory
}
import org.locationtech.spatial4j.shape.{Point, Shape}
import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.intersection.IntersectionManager
import pl.edu.agh.wmazur.avs.model.entity.road.{Lane, RoadManager}
import pl.edu.agh.wmazur.avs.model.entity.utils.{MathUtils, SpatialUtils}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.Angle
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.{
  AutonomousVehicleDriver,
  VehicleDriver
}

object DriversFetcherAgent {
  // format: off
  import SpatialUtils._
  private case class Context(
                              context: ActorContext[Protocol],
                              transmitionArea: PreparedGeometry,
                              intersectionCenter: Point,
                              intersectionGeometry: Geometry,
                              entryPoints: Map[Lane, Point],
                              exitPoints: Map[Lane, Point],
                              intersectionManagerRef: ActorRef[IntersectionManager.Protocol],
                              lanesOccupationAdapter: ActorRef[RoadManager.LanesOccupation],
                              driversPositionAdapter: ActorRef[AutonomousVehicleDriver.BasicReading]
                            )

  sealed trait Protocol extends IntersectionManager.Protocol
  case class FetchDrivers(
                           roadManagers: Iterable[ActorRef[RoadManager.Protocol]])
    extends Protocol
  case class LanesOccupation(
                              roadManagerRef: ActorRef[RoadManager.Protocol],
                              vehiclesAtLanes: Map[Lane, Set[ActorRef[AutonomousVehicleDriver.Protocol]]])
    extends Protocol
  case class DriverReading(
                            driverRef: ActorRef[AutonomousVehicleDriver.Protocol],
                            position: Point,
                            heading: Angle)
    extends Protocol

  def init(
            intersectionPosition: Point,
            intersectionGeometry: Geometry,
            entryPoints: Map[Lane, Point],
            exitPoints: Map[Lane, Point],
            transmitionDistance: Dimension,
            intersectionManager: ActorRef[IntersectionManager.Protocol]): Behavior[Protocol] =
  Behaviors.setup { ctx =>
    // format: on
    val transmitionArea = new PreparedGeometryFactory().create {
      SpatialUtils.shapeFactory.getGeometryFrom {
        intersectionPosition.getBuffered(
          transmitionDistance.asGeoDegrees,
          SpatialUtils.shapeFactory.getSpatialContext)
      }
    }
    val lanesOccupationAdapter =
      ctx.messageAdapter[RoadManager.LanesOccupation] {
        case RoadManager.LanesOccupation(ref, vehiclesAtLanes) =>
          LanesOccupation(ref, vehiclesAtLanes)
      }

    val driversPositionAdapter: ActorRef[AutonomousVehicleDriver.BasicReading] =
      ctx.messageAdapter[AutonomousVehicleDriver.BasicReading] {
        case AutonomousVehicleDriver.BasicReading(ref,
                                                  position,
                                                  heading,
                                                  _,
                                                  _,
                                                  _) =>
          DriverReading(ref, position, heading)
      }

    idle {
      Context(
        context = ctx,
        transmitionArea = transmitionArea,
        intersectionCenter = intersectionPosition,
        entryPoints = entryPoints,
        exitPoints = exitPoints,
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
  // format: off
  def waitForDriversAtLanes(
                             context: Context,
                             awaitingManagers: Set[ActorRef[RoadManager.Protocol]],
                             driversAtLanes: Map[Lane,Set[ActorRef[AutonomousVehicleDriver.Protocol]]])
  : Behavior[Protocol] = {
    if (awaitingManagers.isEmpty) {
      val drivers = driversAtLanes.values.flatten.toSet
      drivers.foreach { ref =>
        ref ! AutonomousVehicleDriver.GetPositionReading(
          context.driversPositionAdapter)
        context.context.watch(ref)
      }

      val driversOccupation: Map[ActorRef[AutonomousVehicleDriver.Protocol], Set[Lane]] = {
        for {
          (lane, drivers) <- driversAtLanes
          driver <- drivers
        } yield driver -> lane
      }.groupBy(_._1)
        .mapValues(_.values.toSet)

      waitForDriversReadings(context, drivers, driversOccupation)
    } else {
      Behaviors.receiveMessagePartial[Protocol] {
        case LanesOccupation(roadManagerRef, vehiclesAtLanes) =>
          waitForDriversAtLanes(
            context = context,
            awaitingManagers = awaitingManagers - roadManagerRef,
            driversAtLanes = driversAtLanes ++ vehiclesAtLanes)
      }.orElse(Behaviors.ignore)
    }
  }
  def waitForDriversReadings(
                              context: Context,
                              awaitingDrivers: Set[ActorRef[AutonomousVehicleDriver.Protocol]],
                              driverLanesOccupation: Map[ActorRef[AutonomousVehicleDriver.Protocol], Set[Lane]]
                            ): Behavior[Protocol] = {
    // format: on
    if (awaitingDrivers.isEmpty) {
      idle(context)
    } else {
      Behaviors
        .receiveMessagePartial[Protocol] {
          case DriverReading(driverRef, driverPosition, heading) =>
            val positionJTS = SpatialUtils.jtsGeometryFactory.createPoint(
              new Coordinate(driverPosition.getX, driverPosition.getY))
            def withinArea: Boolean =
              context.transmitionArea.contains(positionJTS)

            def headingToIntersection: Boolean = {
              val angle = driverPosition.angle(context.intersectionCenter) - heading
              MathUtils.recenter(angle, 0, 2 * Math.PI) < Math.PI / 3 ||
              MathUtils.recenter(angle.abs, 0, 2 * Math.PI) < Math.PI / 3

            }
            if (withinArea && headingToIntersection) {
              val lane = driverLanesOccupation(driverRef).minBy(
                _.distanceFromPoint(driverPosition))
              val position = context.entryPoints
                .get(lane)
                .orElse(context.entryPoints.find(_._1.id == lane.id).map(_._2))
                .get

              driverRef
                .unsafeUpcast[VehicleDriver.Protocol] ! VehicleDriver.Protocol
                .IntersectionManagerInRange(context.intersectionManagerRef,
                                            position,
                                            context.intersectionGeometry)
            }

            context.context.unwatch(driverRef)
            waitForDriversReadings(context,
                                   awaitingDrivers - driverRef,
                                   driverLanesOccupation)
        }
        .receiveSignal {
          case (
              _,
              Terminated(
                ref: ActorRef[AutonomousVehicleDriver.Protocol] @unchecked)) =>
            waitForDriversReadings(context,
                                   awaitingDrivers - ref,
                                   driverLanesOccupation)
        }
        .orElse(Behaviors.ignore)
    }

  }

}
