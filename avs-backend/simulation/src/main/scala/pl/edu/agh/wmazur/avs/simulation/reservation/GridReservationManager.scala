package pl.edu.agh.wmazur.avs.simulation.reservation

import java.util.concurrent.TimeUnit

import pl.edu.agh.wmazur.avs.model.entity.intersection.Intersection
import pl.edu.agh.wmazur.avs.model.entity.vehicle.{
  BasicVehicle,
  Vehicle,
  VehicleSpec
}
import pl.edu.agh.wmazur.avs.simulation.map.micro.{Tile, TilesGrid}
import pl.edu.agh.wmazur.avs.simulation.reservation.GridReservationManager._
import pl.edu.agh.wmazur.avs.simulation.reservation.GridReservationManager.ReservationSchedule.AccelerationEvent
import pl.edu.agh.wmazur.avs.simulation.reservation.ReservationArray.{
  TimeTile,
  Timestamp
}
import com.softwaremill.quicklens._
import pl.edu.agh.wmazur.avs.model.entity.road.Lane
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{
  Acceleration,
  Velocity
}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.movement.VelocityReachingMovement
import pl.edu.agh.wmazur.avs.simulation.driver.{CrashTestDummy, VehicleDriver}

import scala.annotation.tailrec
import scala.collection._
import scala.concurrent.duration._

class GridReservationManager(config: ManagerConfig,
                             intersection: Intersection) {

  val tilesGrid = TilesGrid(
    intersection.area.getBoundingBox,
    config.granularity
  )
  val reservationGrid = ReservationGrid(tilesGrid, 10.seconds)

  //acceptPlan
  //cancelPlan

  def scheduleTrajectory(
      query: ReservationQuery): Option[ReservationSchedule] = {
    val arrivalLane = intersection.lanesById(query.arrivalLaneId)
    val departueLane = intersection.lanesById(query.departureLaneId)

    val testVehicle = createTestVehicle(vehicle = query.vehicle,
                                        arrivalVelocity = query.arrivalVelocity,
                                        maxVelocity = query.maxTurnVelocity,
                                        arrivalLane = arrivalLane)
    val testDriver = CrashTestDummy(testVehicle, arrivalLane, departueLane)

    findTimeTilesPath(vehicle = testVehicle,
                      driver = testDriver,
                      arrivalTime = query.arrivalTime,
                      isAccelerating = query.isAccelerating)
      .collect {
        case (tiles, exitTime) =>
          val accelerationProfile = calculateAccelerationProfile(
            arrivalTime = query.arrivalTime,
            arrivalVelocity = query.arrivalVelocity,
            maxVelocity = query.maxTurnVelocity,
            maxAcceleration = testVehicle.spec.maxAcceleration,
            exitTime = exitTime,
            isAccelerating = query.isAccelerating
          )

          ReservationSchedule(
            vin = query.vin,
            exitTime = exitTime,
            exitVelocity = testVehicle.velocity,
            tilesCovered = tiles.toList,
            accelerationSchedule = accelerationProfile
          )
      }
  }

  private def findTimeTilesPath(
      vehicle: Vehicle,
      driver: VehicleDriver,
      arrivalTime: Timestamp,
      isAccelerating: Boolean): Option[(Set[TimeTile], Timestamp)] = {
    assert {
      intersection.bufferedArea.relate(vehicle.pointAtMiddleFront).intersects()
    }

    @tailrec
    def iterate(driver: VehicleDriver,
                currentTime: Timestamp,
                timeTiles: Set[TimeTile] = Set.empty)
      : Option[(Set[TimeTile], Timestamp)] = {
      val vehicle = driver.vehicle
      val vehicleWithinIntersectionArea = {
        val positionIntersects =
          intersection.bufferedArea.relate(vehicle.position).intersects()

        lazy val rearIntersects =
          intersection.bufferedArea
            .relate {
              vehicle.spec.pointBetweenBackWheels(
                vehicle.position,
                vehicle.heading
              )
            }
            .intersects()
        lazy val intersectsAtAnyPoint =
          intersection.bufferedArea.relate(vehicle.bufferedArea).intersects()

        positionIntersects || rearIntersects || intersectsAtAnyPoint
      }

      if (vehicleWithinIntersectionArea) {
        val newDriverState =
          moveTestVehicle(
            driver,
            ReservationArray.timeStep,
            isAccelerating
          )

        import scala.collection.breakOut

        val tilesToReservation: Set[TimeTile] = tilesGrid
          .occupiedByShape(vehicle.bufferedArea)
          .flatMap { tile =>
            val timeBuffer = calculateTimeBuffer(tile).toMillis
            val timeRange = (currentTime - timeBuffer) to (currentTime + timeBuffer)
            timeRange.map(timestamp => TimeTile(tile.id, timestamp))
          }(breakOut)

        if (tilesToReservation.exists(
              tt => reservationGrid.isReservedAt(tt.timestamp, tt.tileId))) {
          None
        } else {
          iterate(
            newDriverState,
            currentTime + ReservationArray.timeStepMillis,
            timeTiles ++ tilesToReservation
          )
        }
      } else {
        Some((timeTiles, currentTime))
      }
    }

    iterate(
      driver,
      reservationGrid.firstAvailableTimestamp(arrivalTime)
    )
  }

  private def moveTestVehicle(driver: VehicleDriver,
                              duration: FiniteDuration,
                              isAccelerating: Boolean): VehicleDriver = {
    import TimeUnit._
    driver
      .prepareToMove()
      .updateVehicle {
        case v: Vehicle with VelocityReachingMovement if isAccelerating =>
          v.maxAccelerationAndTargetVelocity
            .move(duration.toUnit(SECONDS))
            .asInstanceOf[Vehicle]
        case v: Vehicle with VelocityReachingMovement =>
          v.withSteadyVelocity
            .move(duration.toUnit(SECONDS))
            .asInstanceOf[Vehicle]
        case _ =>
          throw new UnsupportedOperationException(
            "Cannot set acceleration for non VelocityReachinMovement")
      }
  }

  private def createTestVehicle(vehicle: Vehicle,
                                arrivalVelocity: Velocity,
                                maxVelocity: Velocity,
                                arrivalLane: Lane): Vehicle = {
    vehicle match {
      case v: BasicVehicle =>
        v.withPosition(intersection.entryPoints(arrivalLane))
          .withHeading(intersection.entryHeadings(arrivalLane))
          .withVelocity(arrivalVelocity)
          .modify(_.spec.maxVelocity)
          .setTo(maxVelocity)
          .modifyAll(_.spec.wheelRadius,
                     _.spec.wheelWidth,
                     _.steeringAngle,
                     _.acceleration)
          .setTo(0)
    }
  }

  private def calculateTimeBuffer(tile: Tile): FiniteDuration = {
    if (config.enableEdgeTimeBuffer && tile.isEdge) {
      config.edgeTimeBuffer
    } else {
      config.internalTimeBuffer
    }
  }

  private def calculateAccelerationProfile(
      arrivalTime: Timestamp,
      arrivalVelocity: Velocity,
      maxVelocity: Velocity,
      maxAcceleration: Acceleration,
      exitTime: Timestamp,
      isAccelerating: Boolean): List[AccelerationEvent] = {
    val travelsalTime = exitTime - arrivalTime
    assert(travelsalTime > 0)
    if (isAccelerating && (arrivalVelocity < maxVelocity)) {
      val accelerationDuration =
        Math.min((maxVelocity - arrivalVelocity) / maxAcceleration,
                 travelsalTime)

      val accelerationPart =
        AccelerationEvent(maxAcceleration, accelerationDuration.millis)
      val constVelocityPart = if (accelerationDuration < travelsalTime) {
        AccelerationEvent(0d, (travelsalTime - accelerationDuration).millis) :: Nil
      } else {
        Nil
      }
      accelerationPart :: constVelocityPart
    } else {
      AccelerationEvent(0d, travelsalTime.millis) :: Nil
    }

  }

}

object GridReservationManager {
  type Vin = Int
  type LaneId = Int

  case class ManagerConfig(
      timeStep: Int,
      timeBuffer: FiniteDuration,
      internalTimeBuffer: FiniteDuration,
      edgeTimeBuffer: FiniteDuration,
      enableEdgeTimeBuffer: Boolean = true,
      granularity: Float
  )

  case class ReservationQuery(
      vin: Vin,
      arrivalTime: Timestamp,
      arrivalVelocity: Velocity,
      maxTurnVelocity: Velocity,
      arrivalLaneId: LaneId,
      departureLaneId: LaneId,
      vehicle: Vehicle,
      isAccelerating: Boolean
  )

  case class ReservationSchedule(
      vin: Vin,
      exitTime: Timestamp,
      exitVelocity: Velocity,
      tilesCovered: List[TimeTile],
      accelerationSchedule: List[AccelerationEvent]
  )

  object ReservationSchedule {
    case class AccelerationEvent(acceleration: Double, duration: FiniteDuration)
  }
}
