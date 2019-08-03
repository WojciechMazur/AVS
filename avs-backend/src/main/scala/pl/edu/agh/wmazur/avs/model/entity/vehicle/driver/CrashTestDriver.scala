package pl.edu.agh.wmazur.avs.model.entity.vehicle.driver

import java.util.concurrent.TimeUnit

import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.road.{Lane, Road}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.Vehicle
import pl.edu.agh.wmazur.avs.model.entity.vehicle.movement.SteeringMovement

import scala.annotation.tailrec
import scala.collection.mutable
import scala.concurrent.duration.FiniteDuration

case class CrashTestDriver(vehicle: Vehicle,
                           var currentLane: Lane,
                           destinationLane: Lane)
    extends VehicleDriver {

  override val destination: Option[Road] = None

  override val occupiedLanes: mutable.Set[Lane] = mutable.Set(currentLane)

  override def prepareToMove(): CrashTestDriver = {
    val self = super.prepareToMove().asInstanceOf[CrashTestDriver]
    if (currentLane != destinationLane) {
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

  @tailrec
  final def followCurrentLane(
      leadTime: FiniteDuration = CrashTestDriver.defaultLeadTime)
    : CrashTestDriver = {

    val leadDistance = leadTime.toUnit(TimeUnit.SECONDS) * vehicle.velocity + CrashTestDriver.minimalLeadDistance
    val remainingDistance =
      currentLane.remainingDistanceAlongLane(vehicle.position)
    val shouldAndCanUseNextLane = leadDistance > remainingDistance && currentLane.spec.leadsIntoLane.isDefined

    def follow: CrashTestDriver = {
      val destinationPoint = if (shouldAndCanUseNextLane) {
        currentLane.spec.leadsIntoLane.map { lane =>
          lane.leadPointOf(lane.entryPoint, leadDistance - remainingDistance)
        }.get
      } else {
        currentLane.leadPointOf(vehicle.position, leadDistance)
      }
      val updatedVehicle = vehicle match {
        case v: Vehicle with SteeringMovement =>
          v.moveWheelsTowardPoint(destinationPoint).asInstanceOf[Vehicle]
        case _ =>
          throw new UnsupportedOperationException(
            "Unable to steer vehicle without SteeringMovement ")
      }
      copy(vehicle = updatedVehicle)
    }

    if (shouldAndCanUseNextLane && remainingDistance <= 0d) {
      copy(currentLane = currentLane.spec.leadsIntoLane.get).followCurrentLane()
    } else {
      follow
    }
  }

  def calculateLaneTraversingDeltaDistance: Dimension = {
    CrashTestDriver.traversingLaneChangeLeadTime.toUnit(TimeUnit.SECONDS) * vehicle.velocity
  }

  override protected def withVehicle(vehicle: Vehicle): VehicleDriver =
    copy(vehicle = vehicle)
}

object CrashTestDriver {
  import scala.concurrent.duration._
  val traversingLaneChangeLeadTime: FiniteDuration = 1.5f.seconds
  val defaultLeadTime: FiniteDuration = 0.4f.second
  val minimalLeadDistance: Dimension = 0.2d
}
