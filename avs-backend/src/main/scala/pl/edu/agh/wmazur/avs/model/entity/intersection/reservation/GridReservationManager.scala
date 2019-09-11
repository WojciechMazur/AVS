package pl.edu.agh.wmazur.avs.model.entity.intersection.reservation

import java.util.concurrent.TimeUnit

import akka.actor.typed.ActorRef
import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.intersection.Intersection
import pl.edu.agh.wmazur.avs.model.entity.intersection.IntersectionManager.Protocol.IntersectionCrossingRequest
import pl.edu.agh.wmazur.avs.model.entity.intersection.reservation.GridReservationManager.ManagerConfig
import pl.edu.agh.wmazur.avs.model.entity.intersection.reservation.ReservationArray.{
  ReservationId,
  TimeTile,
  Timestamp
}
import pl.edu.agh.wmazur.avs.model.entity.road.Lane
import pl.edu.agh.wmazur.avs.model.entity.utils.{IdProvider, SpatialUtils}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.AccelerationProfile.AccelerationEvent
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{
  Acceleration,
  Velocity
}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.{
  CrashTestDriver,
  VehicleDriver
}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.movement.VelocityReachingMovement
import pl.edu.agh.wmazur.avs.model.entity.vehicle._
import pl.edu.agh.wmazur.avs.simulation.TickSource

import scala.annotation.tailrec
import scala.collection._
import scala.concurrent.duration._

case class GridReservationManager(config: ManagerConfig,
                                  intersection: Intersection) {
  import GridReservationManager._

  private val tilesGrid = TilesGrid(
    intersection.area,
    config.granularity
  )
  private val reservationGrid =
    ReservationGrid(tilesGrid, config.timeStep, config.timeStep)

  def clean(currentTime: Timestamp): Unit = {
    reservationGrid.cleanup(currentTime)
  }

  def cancel(reservationId: ReservationId): Unit = {
    reservationGrid.cancel(reservationId)
  }

  def accept(reservationSchedule: ReservationSchedule): Option[Long] = {
    val ticket = reservationsIdProvider.nextId
    val wasAccepted =
      reservationGrid.reserve(ticket, reservationSchedule.tilesCovered)

    if (wasAccepted) Some(ticket) else None
  }

  def scheduleTrajectory(
      query: ReservationQuery): Option[ReservationSchedule] = {
    val arrivalLane = intersection.lanesById(query.arrivalLaneId)
    val departueLane = intersection.lanesById(query.departureLaneId)
    val driver = {
      val v = createTestVehicle(requestVehicleSpec = query.vehicleSpec,
                                arrivalVelocity = query.arrivalVelocity,
                                maxVelocity = query.maxTurnVelocity,
                                arrivalLane = arrivalLane)
      CrashTestDriver(v, arrivalLane, departueLane)
    }

    findTimeTilesPath(driver = driver,
                      minimalArrivalTime = query.arrivalTime,
                      isAccelerating = query.isAccelerating)
      .collect {
        case (tiles, arrivalTime, exitTime, exitVelocity) =>
          val accelerationProfile = calculateAccelerationProfile(
            arrivalTime = arrivalTime,
            arrivalVelocity = query.arrivalVelocity,
            maxVelocity = query.maxTurnVelocity,
            maxAcceleration = driver.vehicle.spec.maxAcceleration,
            exitTime = exitTime,
            isAccelerating = query.isAccelerating
          )

          ReservationSchedule(
            driverRef = query.driverRef,
            arrivalTime = arrivalTime,
            exitTime = exitTime,
            exitVelocity = exitVelocity,
            tilesCovered = tiles.toList,
            accelerationProfile = accelerationProfile
          )
      }
  }

  private def findTimeTilesPath(driver: VehicleDriver,
                                minimalArrivalTime: Timestamp,
                                isAccelerating: Boolean)
    : Option[(Set[TimeTile], Timestamp, Timestamp, Velocity)] = {
    assert {
      intersection.bufferedArea
        .relate(driver.vehicle.pointAtMiddleFront)
        .intersects()
    }

    val initialDriverState: CrashTestDriver =
      driver.asInstanceOf[CrashTestDriver].copy()
    val maximalArrivalTime = minimalArrivalTime + 30 * config.timeStepMillis
    @tailrec
    def iterate(driver: VehicleDriver,
                arrivalTime: Timestamp,
                currentTime: Timestamp,
                timeTiles: Set[TimeTile] = Set.empty)
      : Option[(Set[TimeTile], Timestamp, Timestamp, Velocity)] = {
      val vehicle = driver.vehicle
      val vehicleWithinIntersectionArea = {
        intersection.preparedGeometry.intersects(vehicle.geometry)
      }

      if (vehicleWithinIntersectionArea) {
        val newDriverState =
          moveTestVehicle(
            driver,
            config.timeStep,
            isAccelerating
          )

        import scala.collection.breakOut

        val tilesToReservation: Set[TimeTile] = tilesGrid
          .occupiedByGeometry(vehicle.geometry)
          .flatMap { tile =>
            val timeBuffer = calculateTimeBuffer(tile).toMillis
            val timeRange = (currentTime - timeBuffer) to (currentTime + timeBuffer) by config.timeStep.toMillis
            timeRange.map(timestamp => TimeTile(tile.id, timestamp))
          }(breakOut)

        val reservedTiles = tilesToReservation.filter(tt =>
          reservationGrid.isReservedAt(tt.timestamp, tt.tileId))

        if (reservedTiles.nonEmpty) {
//          println(
//            s"reserved, currentTime ${currentTime}, path ${initialDriverState.currentLane.id} ~> ${driver
//              .asInstanceOf[CrashTestDriver]
//              .destinationLane
//              .id}, ${driver.currentLane.spec.turningAllowance} ")
//          reservedTiles.toList
//            .sortBy(t => (t.timestamp, t.tileId))
//            .foreach(t => println(s"\t${t.tileId} @ ${t.timestamp}"))
          val nextTestedArrivalTime = reservedTiles
            .map(reservationGrid.firstAvailableTimestamp)
            .max
            .max(currentTime + config.timeStepMillis)

          if (nextTestedArrivalTime <= maximalArrivalTime) {
            iterate(
              initialDriverState,
              nextTestedArrivalTime,
              nextTestedArrivalTime,
              Set.empty
            )
          } else {
            None
          }

        } else {
          iterate(
            newDriverState,
            arrivalTime,
            currentTime + config.timeStepMillis,
            timeTiles ++ tilesToReservation
          )
        }
      } else {
        Some((timeTiles, arrivalTime, currentTime, vehicle.velocity))
      }
    }

    val arrivalTime =
      reservationGrid.firstAvailableTimestamp(minimalArrivalTime)

    iterate(
      driver,
      arrivalTime,
      arrivalTime
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

  private def createTestVehicle(
      requestVehicleSpec: IntersectionCrossingRequest.CrossingVehicleSpec,
      arrivalVelocity: Velocity,
      maxVelocity: Velocity,
      arrivalLane: Lane): BasicVehicle = {
    val position = intersection.entryPoints(arrivalLane)
    val heading = intersection.entryHeadings(arrivalLane)
    val spec = requestVehicleSpec.toVehicleSpec(maxVelocity)
    VirtualVehicle(
      gauges = VehicleGauges(position,
                             arrivalVelocity,
                             0,
                             0,
                             heading,
                             Vehicle.calcGeometry(position, heading, spec)),
      spec = spec,
      targetVelocity = 0,
      spawnTime = -1
    )
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
      isAccelerating: Boolean): AccelerationProfile = {
    val travelsalTime = exitTime - arrivalTime
    assert(travelsalTime > 0)
    val events = if (isAccelerating && (arrivalVelocity < maxVelocity)) {
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
    AccelerationProfile(events)
  }

}

object GridReservationManager {
  val reservationsIdProvider: IdProvider[ReservationId] =
    new IdProvider[ReservationId] {}
  case class ManagerConfig(timeStep: FiniteDuration,
                           granularity: Dimension,
                           enableEdgeTimeBuffer: Boolean = false)(
      _internalTimeBuffer: FiniteDuration = timeStep,
      _edgeTimeBuffer: FiniteDuration = timeStep,
  ) {
    val internalTimeBuffer: FiniteDuration = _internalTimeBuffer.max(timeStep)
    val edgeTimeBuffer: FiniteDuration = _edgeTimeBuffer.max(timeStep)
    val timeStepMillis: Timestamp = timeStep.toMillis
  }

  case class ReservationQuery(
      driverRef: ActorRef[VehicleDriver.Protocol],
      arrivalTime: Timestamp,
      arrivalVelocity: Velocity,
      maxTurnVelocity: Velocity,
      arrivalLaneId: Lane#Id,
      departureLaneId: Lane#Id,
      vehicleSpec: IntersectionCrossingRequest.CrossingVehicleSpec,
      isAccelerating: Boolean
  ) {
    require(arrivalVelocity != 0f || isAccelerating,
            "Unable to schedule stopped, non accelerating vehicle")
  }

  case class ReservationSchedule(
      driverRef: ActorRef[VehicleDriver.Protocol],
      arrivalTime: Timestamp,
      exitTime: Timestamp,
      exitVelocity: Velocity,
      tilesCovered: List[TimeTile],
      accelerationProfile: AccelerationProfile
  )

}
