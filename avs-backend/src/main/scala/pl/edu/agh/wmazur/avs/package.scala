package pl.edu.agh.wmazur

import akka.actor.typed.receptionist.ServiceKey
import org.locationtech.spatial4j.distance.DistanceUtils
import pl.edu.agh.wmazur.avs.http.management.WebsocketManager
import pl.edu.agh.wmazur.avs.model.entity.intersection.IntersectionManager
import pl.edu.agh.wmazur.avs.model.entity.road.RoadManager
import pl.edu.agh.wmazur.avs.model.entity.utils.MathUtils.DoubleUtils
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

  implicit class Dimension(val asMeters: Double)
      extends AnyVal
      with Ordered[Dimension] {
    def geoDegrees: Dimension =
      Dimension(asMeters * DistanceUtils.DEG_TO_KM * 1000)
    def meters: Dimension = Dimension(asMeters)

    def asGeoDegrees: Double = asMeters / 1000 * DistanceUtils.KM_TO_DEG

    //scalastyle:off
    def unary_- : Dimension = Dimension(-asMeters)
    def -(that: Dimension): Dimension = Dimension(this.asMeters - that.asMeters)
    def +(that: Dimension): Dimension = Dimension(this.asMeters + that.asMeters)
    def *(that: Dimension): Dimension = Dimension(this.asMeters * that.asMeters)
    def /(that: Dimension): Dimension = Dimension(this.asMeters / that.asMeters)
    def sqrt: Dimension = Math.sqrt(asMeters)
    //scalastyle:on

    def isEqual(that: Dimension): Boolean = {
      this.asMeters isEqual that.asMeters
    }
    override def compare(that: Dimension): Int =
      this.asMeters compare that.asMeters

  }

  object Dimension {
    implicit val numeric: Numeric[Dimension] = new Numeric[Dimension] {
      override def plus(x: Dimension, y: Dimension): Dimension =
        Dimension(x.asMeters + y.asMeters)

      override def minus(x: Dimension, y: Dimension): Dimension =
        Dimension(x.asMeters - y.asMeters)

      override def times(x: Dimension, y: Dimension): Dimension =
        Dimension(x.asMeters * y.asMeters)

      override def negate(x: Dimension): Dimension = Dimension(-x.asMeters)

      override def fromInt(x: Int): Dimension = Dimension(x.toDouble)

      override def toInt(x: Dimension): Int = x.asMeters.toInt

      override def toLong(x: Dimension): Long = x.asMeters.toLong

      override def toFloat(x: Dimension): Float = x.asMeters.toFloat

      override def toDouble(x: Dimension): Double = x.asMeters

      override def compare(x: Dimension, y: Dimension): Int =
        x.asMeters compare y.asMeters
    }
  }

  case class Tick(seq: Long) extends AnyVal {
    def next: Tick = Tick(seq + 1)
  }

}
