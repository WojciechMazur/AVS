//package pl.edu.agh.wmazur.avs.simulation.driver
//
//import java.util.concurrent.TimeUnit
//
//import org.locationtech.spatial4j.shape.Point
//import pl.edu.agh.wmazur.avs.model.entity.intersection.IntersectionManager
//import pl.edu.agh.wmazur.avs.model.entity.road.{Lane, Road}
//import pl.edu.agh.wmazur.avs.model.entity.vehicle.Vehicle
//import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.Dimension
//import pl.edu.agh.wmazur.avs.model.entity.vehicle.movement.SteeringMovement
//
//import scala.annotation.tailrec
//import scala.collection.mutable
//import scala.concurrent.duration.FiniteDuration
//
//case class CrashTestDummy(vehicle: Vehicle,
//                          var currentLane: Lane,
//                          destinationLane: Lane)
//    extends VehicleDriver {
//
//  override def destination: Option[Road] = None
//
//  override def nextIntersectionManager: Option[IntersectionManager] = None
//
//  override def prevIntersectionManager: Option[IntersectionManager] = None
//  override def distanceToNextIntersection: Dimension = -1
//
//  override def distanceToPrevIntersection: Dimension = -1
//  override val occupiedLanes: mutable.Set[Lane] = mutable.Set(currentLane)
//
//  override def prepareToMove(): CrashTestDummy = {
//    val self = super.prepareToMove().asInstanceOf[CrashTestDummy]
//    if (currentLane != destinationLane) {
//      if (currentLane.spec.road != destinationLane.spec.road) {
//        if (destinationLane.distanceFromPoint(vehicle.position) < calculateLaneTraversingDeltaDistance) {
//          self.setCurrentLane(destinationLane)
//        }
//      } else {
//        self.setCurrentLane(destinationLane)
//      }
//    }
//    self.followCurrentLane()
//  }
//
//  @tailrec
//  final def followCurrentLane(
//      leadTime: FiniteDuration = CrashTestDummy.defaultLeadTime)
//    : CrashTestDummy = {
//
//    val leadDistance = leadTime.toUnit(TimeUnit.SECONDS) * vehicle.velocity + CrashTestDummy.minimalLeadDistance
//    val remainingDistance =
//      currentLane.remainingDistanceAlongLane(vehicle.position)
//    val shouldAndCanUseNextLane = leadDistance > remainingDistance && currentLane.spec.leadsIntoLane.isDefined
//
//    def follow = {
//      val destinationPoint = if (shouldAndCanUseNextLane) {
//        currentLane.spec.leadsIntoLane.map { lane =>
//          lane.leadPointOf(lane.entryPoint, leadDistance - remainingDistance)
//        }.get
//      } else {
//        currentLane.leadPointOf(vehicle.position, leadDistance)
//      }
//      val updatedVehicle = vehicle match {
//        case v: Vehicle with SteeringMovement =>
//          v.moveWheelsTowardPoint(destinationPoint).asInstanceOf[Vehicle]
//        case _ =>
//          throw new UnsupportedOperationException(
//            "Unable to steer vehicle without SteeringMovement ")
//      }
//
//      copy(vehicle = updatedVehicle)
//    }
//
//    if (shouldAndCanUseNextLane && remainingDistance <= 0) {
//      copy(currentLane = currentLane.spec.leadsIntoLane.get).followCurrentLane()
//    } else {
//      follow
//    }
//  }
//
//  def calculateLaneTraversingDeltaDistance: Dimension = {
//    CrashTestDummy.traversingLaneChangeLeadTime.toUnit(TimeUnit.SECONDS) * vehicle.velocity
//  }
//
//  override protected def withVehicle(vehicle: Vehicle): VehicleDriver =
//    copy(vehicle = vehicle)
//}
//
//object CrashTestDummy {
//  import scala.concurrent.duration._
//  val traversingLaneChangeLeadTime: FiniteDuration = 1.5f.seconds
//  val defaultLeadTime: FiniteDuration = 0.4f.second
//  val minimalLeadDistance: Dimension = 0.2f
//}
