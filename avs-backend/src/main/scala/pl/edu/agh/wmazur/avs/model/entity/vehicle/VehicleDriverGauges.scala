package pl.edu.agh.wmazur.avs.model.entity.vehicle

import pl.edu.agh.wmazur.avs.Dimension

case class VehicleDriverGauges(distanceToCarInFront: Option[Dimension],
                               distanceToNextIntersection: Option[Dimension],
                               distanceToPrevIntersection: Option[Dimension])

object VehicleDriverGauges {
  val empty = VehicleDriverGauges(distanceToCarInFront = None,
                                  distanceToNextIntersection = None,
                                  distanceToPrevIntersection = None)
}
