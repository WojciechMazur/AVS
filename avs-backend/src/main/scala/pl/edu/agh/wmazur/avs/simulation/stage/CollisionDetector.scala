package pl.edu.agh.wmazur.avs.simulation.stage

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import org.locationtech.jts.geom.Polygonal
import org.locationtech.jts.geom.prep.{PreparedGeometry, PreparedPolygon}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.Vehicle
import pl.edu.agh.wmazur.avs.model.state.SimulationState

import scala.collection.Set

object CollisionDetector {
  sealed trait Protocol
  case class DetectCollisions(state: SimulationState) extends Protocol

  def init: Behavior[Protocol] = Behaviors.receive {
    case (_, DetectCollisions(state)) =>
      val detectedCollisions = for {
        intersection <- state.intersections.values
        geometry = new PreparedPolygon(
          intersection.geometry.asInstanceOf[Polygonal])
        vehiclesAtIntersection = state.vehicles.values.filter { vehicle =>
          geometry.intersects(vehicle.geometry)
        }.toSet
        intersectingVehicles = vehiclesAtIntersection
          .subsets(2)
          .foldLeft(Set.empty[Vehicle]) {
            case (acc, combination)
                if combination.head.area
                  .relate(combination.last.area)
                  .intersects =>
              acc ++ combination
            case (acc, _) => acc
          }
      } yield intersection.id -> intersectingVehicles
      detectedCollisions
        .filter(_._2.nonEmpty)
        .foreach {
          case (intersection, vehicles) =>
            System.err.println(
              s"Collisions at intersection $intersection between vehicles ${vehicles.map(_.id).mkString(", ")}")
        }
      Behaviors.same
  }

}
