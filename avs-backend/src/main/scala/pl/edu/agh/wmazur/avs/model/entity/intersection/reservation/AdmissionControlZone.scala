package pl.edu.agh.wmazur.avs.model.entity.intersection.reservation

import akka.actor.typed.ActorRef
import com.softwaremill.quicklens._
import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.VehicleDriver

case class AdmissionControlZone(
    controlledDistance: Dimension,
    currentSize: Dimension = 0.meters,
    admittedDriversLength: Map[ActorRef[VehicleDriver.Protocol], Dimension] =
      Map.empty) {
  import AdmissionControlZone._
  type DriverRef = ActorRef[VehicleDriver.Protocol]

  def isAdmissible(driverRef: DriverRef,
                   vehicleLength: Dimension,
                   stoppingDistance: Dimension): Boolean = {
    val spaceNeeded = currentSize + vehicleLength + stoppingDistance
    val wasNotRegistered = !admittedDriversLength.contains(driverRef)
    val enoughFreeSpace = spaceNeeded <= controlledDistance

    wasNotRegistered && enoughFreeSpace
  }

  def admit(driverRef: DriverRef,
            vehicleLength: Dimension,
            stoppingDistance: Dimension): AdmissionControlZone = {
    val reservationLength = vehicleLength + minimalDistanceBetweenCars
    this
      .modify(_.currentSize)
      .using(_ + reservationLength)
      .modify(_.admittedDriversLength)
      .using(_ + (driverRef -> reservationLength))
  }

  def cancel(driverRef: DriverRef): AdmissionControlZone = retain(driverRef)

  def retain(driverRef: DriverRef): AdmissionControlZone = {
    {
      for {
        length <- admittedDriversLength.get(driverRef)
        newState = this
          .modify(_.admittedDriversLength)
          .using(_ - driverRef)
          .modify(_.currentSize)
          .using(_ - length)
      } yield newState
    }.getOrElse {
      this
    }
  }

}

object AdmissionControlZone {
  val minimalDistanceBetweenCars: Dimension = 0.5.meters
  case class Plan(driverRef: ActorRef[VehicleDriver.Protocol],
                  vehicleLength: Dimension,
                  stoppingDistance: Dimension)
}
