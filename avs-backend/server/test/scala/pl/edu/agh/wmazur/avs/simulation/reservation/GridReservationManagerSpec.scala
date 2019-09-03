package pl.edu.agh.wmazur.avs.model.entity.intersection.reservation

import org.locationtech.spatial4j.context.SpatialContext
import org.scalatest.{FlatSpec, WordSpec}
import pl.edu.agh.wmazur.avs.model.entity.intersection.AutonomousRoadIntersection
import pl.edu.agh.wmazur.avs.model.entity.road.{DirectedLane, LaneSpec, Road}
import pl.edu.agh.wmazur.avs.model.entity.utils.SpatialUtils.Point2
import pl.edu.agh.wmazur.avs.model.entity.vehicle.BasicVehicle
import pl.edu.agh.wmazur.avs.simulation.driver.CrashTestDriver
import pl.edu.agh.wmazur.avs.model.entity.intersection.reservation.GridReservationManager.ManagerConfig

import scala.concurrent.duration._

class GridReservationManagerSpec extends WordSpec {
  val laneSpec = new LaneSpec(20, 2.8)
  val lane1 = DirectedLane(laneSpec)(Point2(-10, 0), Point2(10, 0))
  val lane2 = DirectedLane(laneSpec)(Point2(0, -10), Point2(0, 10))

  val road1 = Road(lane1 :: Nil)
  val road2 = Road(lane2 :: Nil)

  val intersection = RoadIntersection(road1 :: road2 :: Nil)
  val managerConfig = ManagerConfig(
    timeStep = 1.second / 30,
    granularity = 1.0f
  )()

//  val driver1 = CrashTestDriver(vehicle1, lane1, lane2)
  val vehicle1 = BasicVehicle(
    position = intersection.entryPoints(lane1),
    heading = intersection.entryHeadings(lane1),
    velocity = 10.0
  )

  val vehicle2 = BasicVehicle(
    position = intersection.entryPoints(lane1),
    heading = intersection.entryHeadings(lane1),
    velocity = 10.0
  )

  "Intersection" when {
    "initialization" must {
      "be correctly initialized" in {

        assert(intersection.entryPoints.size == 2)
        assert(intersection.entryRoads.size == 2)
        assert(intersection.area.getArea(SpatialContext.GEO) > 0)
      }
    }

    "empty intersection" must {
      val gridReservationManager =
        GridReservationManager(managerConfig, intersection)

      "schedule moving vehicle" in {
        val query = GridReservationManager.ReservationQuery(
          vin = vehicle1.id,
          arrivalTime = vehicle1.spawnTime,
          arrivalVelocity = vehicle1.velocity,
          maxTurnVelocity = vehicle1.spec.maxVelocity / 2,
          arrivalLaneId = lane1.id,
          departureLaneId = lane2.id,
          vehicle = vehicle1,
          isAccelerating = true
        )

        val result = gridReservationManager.scheduleTrajectory(query)
        assert(result.nonEmpty)
      }
      "schedule stopped vehicle" in {
        val gridReservationManager =
          GridReservationManager(managerConfig, intersection)

        val query = GridReservationManager.ReservationQuery(
          vin = vehicle1.id,
          arrivalTime = vehicle1.spawnTime,
          arrivalVelocity = 0,
          maxTurnVelocity = vehicle1.spec.maxVelocity / 2,
          arrivalLaneId = lane1.id,
          departureLaneId = lane2.id,
          vehicle = vehicle1.withVelocity(0),
          isAccelerating = true
        )
        val result = gridReservationManager.scheduleTrajectory(query)
        assert(result.nonEmpty)
        assert(result.get.exitTime > vehicle1.spawnTime)
        assert(result.get.exitVelocity > 0)
      }
    }
    "multiple vehicles" must {
      "schedule vehicles from same lane" in {
        val gridReservationManager =
          GridReservationManager(managerConfig, intersection)

        val query1 = GridReservationManager.ReservationQuery(
          vin = vehicle1.id,
          arrivalTime = vehicle1.spawnTime,
          arrivalVelocity = vehicle1.velocity,
          maxTurnVelocity = vehicle1.spec.maxVelocity / 2,
          arrivalLaneId = lane1.id,
          departureLaneId = lane2.id,
          vehicle = vehicle1,
          isAccelerating = true
        )

        val query2 = GridReservationManager.ReservationQuery(
          vin = vehicle2.id,
          arrivalTime = vehicle1.spawnTime + 500,
          arrivalVelocity = vehicle2.velocity,
          maxTurnVelocity = vehicle2.spec.maxVelocity / 2,
          arrivalLaneId = lane1.id,
          departureLaneId = lane2.id,
          vehicle = vehicle2,
          isAccelerating = true
        )

        val result1 = gridReservationManager.scheduleTrajectory(query1)
        assert(result1.nonEmpty)
//        result1.map(_.tilesCovered).map(gridReservationManager)
      }
    }
  }
}
