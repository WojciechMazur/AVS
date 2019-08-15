package pl.edu.agh.wmazur.avs.model.entity.vehicle.driver

import com.softwaremill.quicklens._
import org.locationtech.jts.geom.Geometry
import org.locationtech.spatial4j.shape.Point
import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.utils.SpatialUtils
import pl.edu.agh.wmazur.avs.model.entity.vehicle.Vehicle
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.protocol.DriverConnectivity.VehicleCachedReadings

case class VehicleDriverGauges(
    distanceToCarInFront: Option[Dimension],
    distanceToCollisionWithCarInFront: Option[Dimension],
    distanceToNextIntersection: Option[Dimension],
    distanceToPrevIntersection: Option[Dimension],
    isWithinIntersection: Boolean) {

  def calcDistance(point: Point, vehicle: Vehicle): Dimension =
    SpatialUtils.shapeFactory
      .getGeometryFrom(point)
      .distance(vehicle.geometry)
      .geoDegrees

  def updateVehicleInFront(optVehicleInFront: Option[VehicleCachedReadings],
                           vehicle: Vehicle): VehicleDriverGauges = {
    val distancToCarInFront = for {
      vehicleInFront <- optVehicleInFront
      geometry = vehicleInFront.geometry.getOrElse {
        SpatialUtils.shapeFactory.getGeometryFrom(vehicleInFront.position)
      }
    } yield geometry.distance(vehicle.geometry).geoDegrees

    this
      .modify(_.distanceToCarInFront)
      .setTo(distancToCarInFront)
      .modify(_.distanceToCollisionWithCarInFront)
      .setTo {
        for {
          vehicleInFront <- optVehicleInFront
          relativeVelocity = vehicle.velocity - vehicleInFront.velocity
          if relativeVelocity > 0
          distance <- distancToCarInFront
          timeBeforeCollision = distance.asMeters / relativeVelocity
          distanceCovered = vehicle.velocity * timeBeforeCollision
        } yield distanceCovered.meters
      }
  }

  def updateDistanceToIntersections(next: Option[Point],
                                    prev: Option[Point],
                                    vehicle: Vehicle): VehicleDriverGauges = {
    this
      .modify(_.distanceToNextIntersection)
      .setTo {
        next
          .map(SpatialUtils.shapeFactory.getGeometryFrom)
          .map(_.distance(vehicle.geometry).geoDegrees)
      }
      .modify(_.distanceToPrevIntersection)
      .setTo {
        prev
          .map(SpatialUtils.shapeFactory.getGeometryFrom)
          .map(_.distance(vehicle.geometry).geoDegrees)
      }
  }

  def updateIsWithinIntersection(intersectionGeometry: Option[Geometry],
                                 vehicle: Vehicle): VehicleDriverGauges = {
    this
      .modify(_.isWithinIntersection)
      .setTo {
        intersectionGeometry
          .map(_.buffer(0.1.meters.asGeoDegrees))
          .exists(_.relate(vehicle.geometry).isIntersects)
      }
  }
}

object VehicleDriverGauges {
  val empty = VehicleDriverGauges(
    distanceToCarInFront = None,
    distanceToCollisionWithCarInFront = None,
    distanceToNextIntersection = None,
    distanceToPrevIntersection = None,
    isWithinIntersection = false
  )
}
