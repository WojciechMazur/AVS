package pl.edu.agh.wmazur

import akka.actor.typed.receptionist.ServiceKey
import org.locationtech.spatial4j.distance.DistanceUtils
import pl.edu.agh.wmazur.avs.http.management.WebsocketManager
import pl.edu.agh.wmazur.avs.model.entity.intersection.IntersectionManager
import pl.edu.agh.wmazur.avs.model.entity.road.RoadManager
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousVehicleDriver
import pl.edu.agh.wmazur.avs.protocol.SimulationProtocol
import pl.edu.agh.wmazur.avs.simulation.{EntityManager, SimulationManager}

package object avs {

  object Services {
    val webSocketManager: ServiceKey[WebsocketManager.Protocol] =
      ServiceKey("http-endpoint")

    val simulationManager: ServiceKey[SimulationManager.Protocol] =
      ServiceKey("sim-manager")

    val simulationStepAck: ServiceKey[SimulationManager.Protocol.Ack.type] =
      ServiceKey("sim-step-ack")
    val entityManager: ServiceKey[EntityManager.Protocol] =
      ServiceKey("entity-manager")
  }

  object EntityRefsGroup {
    val tickSubscribers: ServiceKey[SimulationProtocol.Tick] =
      ServiceKey("tick-subscribers")
    val road: ServiceKey[RoadManager.Protocol] = ServiceKey("roads-group")
    val driver: ServiceKey[AutonomousVehicleDriver.ExtendedProtocol] =
      ServiceKey("drivers-group")
    val intersection: ServiceKey[IntersectionManager.Protocol] = ServiceKey(
      "intersections-group"
    )
  }

  implicit class Dimension(val meters: Double)
      extends AnyVal
      with Ordered[Dimension] {
    def fromGeoDegrees: Dimension =
      Dimension(meters * DistanceUtils.DEG_TO_KM * 1000)
    def fromMeters: Dimension = Dimension(meters)

    def geoDegrees: Double = meters / 1000 * DistanceUtils.KM_TO_DEG

    //scalastyle:off
    def unary_- : Dimension = Dimension(-meters)
    def -(that: Dimension): Dimension = Dimension(this.meters - that.meters)
    def +(that: Dimension): Dimension = Dimension(this.meters + that.meters)
    def *(that: Dimension): Dimension = Dimension(this.meters * that.meters)
    def /(that: Dimension): Dimension = Dimension(this.meters / that.meters)
    def sqrt: Dimension = Math.sqrt(meters)
    //scalastyle:on

    override def compare(that: Dimension): Int = this.meters compare that.meters
  }

  case class Tick(seq: Long) extends AnyVal {
    def next: Tick = Tick(seq + 1)
  }

}
