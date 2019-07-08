package pl.edu.agh.wmazur.avs.model.entity.road

import org.locationtech.jts.geom.Geometry
import org.locationtech.spatial4j.context.SpatialContext
import org.locationtech.spatial4j.shape._
import org.locationtech.spatial4j.shape.jts.{JtsGeometry, JtsShapeFactory}
import pl.edu.agh.wmazur.avs.model.entity.Entity
import pl.edu.agh.wmazur.avs.model.entity.utils.{DeltaOps, SpatialUtils}

trait Road extends Entity with DeltaOps[Road] {
  def lanes: List[Lane]
  def oppositeRoad: Option[Road]

  override lazy val area: Shape = {
    val shapeFactory = SpatialUtils.shapeFactory
    val geometry: Geometry = lanes
      .map(lane => shapeFactory.getGeometryFrom(lane.area))
      .reduce(_.union(_))
    shapeFactory.makeShape(geometry)

  }

  override def isUpdatedBy(old: Road): Boolean = false
}
