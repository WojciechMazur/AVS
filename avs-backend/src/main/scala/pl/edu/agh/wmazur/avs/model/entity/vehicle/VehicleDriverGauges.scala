package pl.edu.agh.wmazur.avs.model.entity.vehicle

import com.softwaremill.quicklens._
import org.locationtech.spatial4j.shape.Point
import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.utils.SpatialUtils
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.protocol.DriverConnectivity.VehicleCachedReadings

case class VehicleDriverGauges(distanceToCarInFront: Option[Dimension],
                               distanceToNextIntersection: Option[Dimension],
                               distanceToPrevIntersection: Option[Dimension]) {
  def updateVehicleInFront(optVehicleInFront: Option[VehicleCachedReadings],
                           vehicle: Vehicle): VehicleDriverGauges = {
    this
      .modify(_.distanceToCarInFront)
      .setTo {
        for {
          vehicleInFront <- optVehicleInFront
          geometry = vehicleInFront.geometry.getOrElse {
            SpatialUtils.shapeFactory.getGeometryFrom(vehicleInFront.position)
          }
        } yield geometry.distance(vehicle.geometry).geoDegrees
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
}

object VehicleDriverGauges {
  val empty = VehicleDriverGauges(distanceToCarInFront = None,
                                  distanceToNextIntersection = None,
                                  distanceToPrevIntersection = None)
}
