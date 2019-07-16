package pl.edu.agh.wmazur.avs.model.entity.vehicle.driver
import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.intersection.IntersectionManager
import pl.edu.agh.wmazur.avs.model.entity.road.{Lane, Road}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.Vehicle

import scala.collection.mutable

class EmptyDriver(_vehicle: => Vehicle) extends VehicleDriver {
  override var currentLane: Lane = _

  override lazy val vehicle: Vehicle = _vehicle

  override def occupiedLanes: mutable.Set[Lane] = mutable.Set.empty
  override def destination: Option[Road] = None
  override protected def withVehicle(vehicle: Vehicle): VehicleDriver =
    new EmptyDriver(vehicle)
  override def nextIntersectionManager: Option[IntersectionManager] = None
  override def prevIntersectionManager: Option[IntersectionManager] = None

  override def distanceToNextIntersection: Dimension = -1d
  override def distanceToPrevIntersection: Dimension = -1d
}

object EmptyDriver {
  def init(vehicle: => Vehicle): EmptyDriver = new EmptyDriver(vehicle)
}
