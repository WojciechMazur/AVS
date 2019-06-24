package pl.edu.agh.wmazur.avs.model.entity.road

import org.locationtech.spatial4j.shape.Point
import pl.edu.agh.wmazur.avs.model.entity.Identifiable

trait Lane extends Identifiable {
  def spec: LaneSpec

  def entryPoint: Point
  def exitPoint: Point

  def leadsIntoLane: Option[Lane]
  def leadsFromLane: Option[Lane]
  def leftNeighbourLane: Option[Lane]
  def rightNeighbourLane: Option[Lane]

}
