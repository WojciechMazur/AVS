package pl.edu.agh.wmazur.avs.simulation.reservation

import pl.edu.agh.wmazur.avs.model.entity.intersection.Intersection
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec
import pl.edu.agh.wmazur.avs.simulation.map.micro.TilesGrid
import pl.edu.agh.wmazur.avs.simulation.reservation.GridReservationManager._
import pl.edu.agh.wmazur.avs.simulation.reservation.GridReservationManager.ReservationSchedule.AccelerationEvent
import pl.edu.agh.wmazur.avs.simulation.reservation.ReservationArray.{
  TimeTile,
  Timestamp
}

import scala.concurrent.duration._

class GridReservationManager(config: ManagerConfig,
                             intersection: Intersection) {
  val tilesGrid = TilesGrid(
    intersection.area.getBoundingBox,
    config.granularity
  )
  val reservationManager = ReservationGrid(tilesGrid, 10.seconds)

  def scheduleTrajectory(
      query: ReservationQuery): Option[ReservationSchedule] = {
    val arrivalLane = intersection.lanesById(query.arrivalLaneId)
    val departueLane = intersection.lanesById(query.departureLaneId)

    ???
  }

}

object GridReservationManager {
  type Vin = Int
  type Velocity = Double
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
      vehicleSpec: VehicleSpec,
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
