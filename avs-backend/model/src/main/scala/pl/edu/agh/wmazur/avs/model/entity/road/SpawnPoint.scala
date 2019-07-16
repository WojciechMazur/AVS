package pl.edu.agh.wmazur.avs.model.entity.road

import java.util.concurrent.atomic.AtomicInteger

import org.locationtech.spatial4j.shape.Shape
import pl.edu.agh.wmazur.avs.model.entity.utils.SpatialUtils
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousDriver
import pl.edu.agh.wmazur.avs.model.entity.vehicle.{
  BasicVehicle,
  Vehicle,
  VehicleSpec
}
import pl.edu.agh.wmazur.avs.model.state.SimulationState

import scala.concurrent.duration.FiniteDuration
import scala.util.Random

case class SpawnPoint(lane: Lane, spawnInterval: FiniteDuration) {
  private var nextSpawnTime: Int = -1
  private val spawnedCounter = new AtomicInteger(0)

  private val position = lane.position
  private val heading = lane.heading

  val spawnArea: Shape = SpatialUtils.shapeFactory.makeShapeFromGeometry {
    lane
      .getGeometryFraction((5.0 / lane.length.meters).min(0.25), 1)
  }

  def spawned: Int = spawnedCounter.get()

  def trySpawn(state: SimulationState): Option[Vehicle] = {
    val shouldCheckToSpawn = nextSpawnTime <= state.currentTime
    lazy val hasSpaceToSpawn = ! {
      state.vehiclesAtLanes
        .getOrElse(lane, Set.empty)
        .map(state.vehicles)
        .exists(_.area.relate(spawnArea).intersects())
    }
//    val hasSpaceToSpawn = true
    if (shouldCheckToSpawn && hasSpaceToSpawn) {
      nextSpawnTime = state.currentTime + spawnInterval.toMillis.toInt
      spawnedCounter.incrementAndGet()

      val spec = {
        val x = Random.nextDouble().abs
        SpawnPoint.specDistributionStacked
          .dropWhile(_._1 < x)
          .head
          ._2
      }
      lazy val driver: AutonomousDriver = new AutonomousDriver(
        vehicle,
        lane
      )

      lazy val vehicle = BasicVehicle(
        position = position,
        optDriver = Some(driver),
        heading = heading,
        velocity = lane.spec.speedLimit,
        spec = spec,
        spawnTime = state.currentTime
      )
      println(
        s"Spawning vehicle ${vehicle.id} at lane ${lane.id} @ ${vehicle.position}")

      Some(vehicle)
    } else { None }
  }
}

object SpawnPoint {
  val specDistributionStacked: Map[Double, VehicleSpec] = Map(
    0.4 -> VehicleSpec.Predefined.Sedan,
    0.8 -> VehicleSpec.Predefined.Coupe,
    1.0 -> VehicleSpec.Predefined.Van
  )

}
