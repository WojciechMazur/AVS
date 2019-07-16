package pl.edu.agh.wmazur.avs.model.entity.vehicle.driver
import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.intersection.IntersectionManager
import pl.edu.agh.wmazur.avs.model.entity.road.{Lane, Road}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.Vehicle

import scala.collection.mutable

class AutonomousDriver(_vehicle: => Vehicle, var currentLane: Lane)
    extends VehicleDriver {
  override lazy val vehicle: Vehicle = _vehicle

  override val occupiedLanes: mutable.Set[Lane] = mutable.Set.empty
  override def destination: Option[Road] = None

  override protected def withVehicle(vehicle: Vehicle): VehicleDriver =
    new AutonomousDriver(vehicle, currentLane)

  override def nextIntersectionManager: Option[IntersectionManager] = None
  override def prevIntersectionManager: Option[IntersectionManager] = None

  override def distanceToNextIntersection: Dimension = -1.0
  override def distanceToPrevIntersection: Dimension = -1.0
}
