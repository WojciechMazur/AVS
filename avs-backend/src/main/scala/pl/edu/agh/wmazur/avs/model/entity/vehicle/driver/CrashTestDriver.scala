package pl.edu.agh.wmazur.avs.model.entity.vehicle.driver

import java.util.concurrent.TimeUnit

import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.road.{Lane, Road}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.{
  BasicVehicle,
  Vehicle,
  driver
}

import scala.annotation.tailrec
import scala.collection.mutable
import scala.concurrent.duration.FiniteDuration

case class CrashTestDriver(var vehicle: BasicVehicle,
                           var currentLane: Lane,
                           destinationLane: Lane)
    extends VehicleDriver
    with VehiclePilot {

  override val destination: Option[Road#Id] = None

  override val occupiedLanes: mutable.Set[Lane] = mutable.Set(currentLane)

  override def prepareToMove(): CrashTestDriver = {
    val self = super.prepareToMove().asInstanceOf[CrashTestDriver]
    if (currentLane.id != destinationLane.id) {
      if (currentLane.spec.road != destinationLane.spec.road) {
        if (destinationLane.distanceFromPoint(vehicle.position) < calculateLaneTraversingDeltaDistance) {
          self.setCurrentLane(destinationLane)
        }
      } else {
        self.setCurrentLane(destinationLane)
      }
    }
    self.followCurrentLane()
  }

  def calculateLaneTraversingDeltaDistance: Dimension = {
    CrashTestDriver.traversingLaneChangeLeadTime.toUnit(TimeUnit.SECONDS) * vehicle.velocity
  }

  override def withVehicle(vehicle: Vehicle): this.type =
    vehicle match {
      case basicVehicle: BasicVehicle =>
        copy(vehicle = basicVehicle).asInstanceOf[this.type]
    }
}

object CrashTestDriver {
  import scala.concurrent.duration._
  val traversingLaneChangeLeadTime: FiniteDuration = 1.5f.seconds
  val defaultLeadTime: FiniteDuration = 0.4f.second
  val minimalLeadDistance: Dimension = 0.2d
}
