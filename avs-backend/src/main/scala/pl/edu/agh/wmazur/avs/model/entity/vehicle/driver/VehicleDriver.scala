package pl.edu.agh.wmazur.avs.model.entity.vehicle.driver

import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.intersection.IntersectionManager
import pl.edu.agh.wmazur.avs.model.entity.road.{Lane, Road}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.Vehicle

import scala.collection.mutable

/**
  * Responsible for controlling vehicle and communication with intersection managers
  */
trait VehicleDriver {
  def vehicle: Vehicle
  var currentLane: Lane
  def occupiedLanes: mutable.Set[Lane]

//  def spawnPoint: Option[SpawnPoint]
  def destination: Option[Road]
  def prepareToMove(): VehicleDriver = {
    this
  }
  final def updateVehicle(fn: Vehicle => Vehicle): VehicleDriver = {
    withVehicle(fn(vehicle))
  }
  protected def withVehicle(vehicle: Vehicle): VehicleDriver

  def nextIntersectionManager: Option[IntersectionManager]
  def prevIntersectionManager: Option[IntersectionManager]

  def distanceToNextIntersection: Option[Dimension]
  def distanceToPrevIntersection: Option[Dimension]

  protected def setCurrentLane(lane: Lane): Lane = {
    currentLane = lane
    occupiedLanes.clear()
    occupiedLanes.update(lane, included = true)
    lane
  }
}
