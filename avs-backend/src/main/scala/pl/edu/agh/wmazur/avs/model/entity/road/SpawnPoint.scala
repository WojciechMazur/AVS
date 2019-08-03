package pl.edu.agh.wmazur.avs.model.entity.road

import org.locationtech.spatial4j.shape.Shape
import pl.edu.agh.wmazur.avs.model.entity.road.workers.LaneSpawnerWorker.PositionReading
import pl.edu.agh.wmazur.avs.model.entity.utils.SpatialUtils
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec

import scala.concurrent.duration.FiniteDuration
import scala.util.Random

case class SpawnPoint(lane: Lane) {
  val spawnArea: Shape = SpatialUtils.shapeFactory.makeShapeFromGeometry {
    val maxVehicleLength =
      VehicleSpec.Predefined.values.map(_.length).maxBy(_.meters)
    lane
      .getGeometryFraction(0, maxVehicleLength.meters * 2 / lane.length.meters)
      .buffer(0.000001)
  }

  def getRandomSpec: VehicleSpec = {
    val r = Random.nextDouble().abs
    SpawnPoint.specDistributionStacked
      .dropWhile(_._1 < r)
      .head match {
      case (_, spec) => spec
    }
  }
  def canSpawn(readings: Set[PositionReading]): Boolean = {
    def areaIntersects =
      readings
        .exists(
          _.area
            .getBuffered(0.000001, SpatialUtils.shapeFactory.getSpatialContext)
            .relate(spawnArea)
            .intersects())

    def pointIntersects =
      readings.exists(
        _.position
          .getBuffered(0.000001, SpatialUtils.shapeFactory.getSpatialContext)
          .relate(spawnArea)
          .intersects())
    ! { pointIntersects || areaIntersects }
  }
}

object SpawnPoint {
  val specDistributionStacked: Map[Double, VehicleSpec] = Map(
    0.4 -> VehicleSpec.Predefined.Sedan,
    0.8 -> VehicleSpec.Predefined.Coupe,
    1.0 -> VehicleSpec.Predefined.Van
  )
  val maxVehiclesPerHour = 3600
  val spawnInterval: FiniteDuration = {
    import scala.concurrent.duration._
    1.hour / maxVehiclesPerHour
  }
}
