package pl.edu.agh.wmazur.avs.model.entity.intersection

import org.locationtech.spatial4j.shape.Shape
import pl.edu.agh.wmazur.avs.model.entity.road.{Lane, Road}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.Vehicle

trait IntersectionManager {
  def intersection: Intersection
  val id: Intersection#Id = intersection.id

  def manages(lane: Lane): Boolean =
    intersection.roads
      .exists(
        _.lanes
          .exists(_.id == lane.id))

  def manages(road: Road): Boolean =
    intersection.roads
      .exists(_.id == road.id)

  def contains(vehicle: Vehicle): Boolean =
    intersection.area
      .relate(vehicle.area)
      .intersects()

  def intersects(shape: Shape): Boolean =
    intersection.area
      .relate(shape)
      .intersects()

}
