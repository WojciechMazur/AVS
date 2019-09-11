package pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.protocol

import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import com.softwaremill.quicklens._
import pl.edu.agh.wmazur.avs.EntityRefsGroup
import pl.edu.agh.wmazur.avs.model.entity.intersection.IntersectionManager
import pl.edu.agh.wmazur.avs.model.entity.intersection.IntersectionManager.Protocol.ExitedControlZone
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.{
  AutonomousVehicleDriver,
  VehiclePilot
}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.protocol.DriverConnectivity.VehicleCachedReadings
import pl.edu.agh.wmazur.avs.protocol.SimulationProtocol
import pl.edu.agh.wmazur.avs.model.entity.intersection.reservation.ReservationArray.Timestamp
import pl.edu.agh.wmazur.avs.simulation.TickSource

trait OnTick {
  self: AutonomousVehicleDriver =>

  import AutonomousVehicleDriver._

  var currentTime: Timestamp = 0L
  var spawnTime: Option[Timestamp] = None

  val tickAdapter: ActorRef[SimulationProtocol.Tick] =
    context.messageAdapter[SimulationProtocol.Tick] {
      case SimulationProtocol.Tick.Default(time, _, _) => Tick(time)
    }

  context.system.receptionist ! Receptionist.register(
    EntityRefsGroup.tickSubscribers,
    tickAdapter)

  lazy val onTickBehavior: Behavior[AutonomousVehicleDriver.Protocol] =
    Behaviors.receiveMessagePartial {
      case Tick(time) =>
        currentTime = time
        if (spawnTime.isEmpty) {
          spawnTime = Some(time)
        }

        List(
          driverInFront
        ).flatten
          .foreach { driver =>
            context.watchWith(driver.ref, EmptyReading(driver.ref))
            driver.ref ! GetPositionReading(context.self.narrow)
          }

        lastRequestTimestamp
          .filter(
            currentTime - _ > PreperingReservation.reservationRequestTimeout.toMillis)
          .foreach { _ =>
            lastRequestTimestamp = None
            context.self ! ReservationRequestTimeout
          }

        nextReservationRequestAttempt
          .filter(currentTime >= _)
          .filter(_ =>
            driverGauges.distanceToCollisionWithCarInFront.forall(
              _ > VehiclePilot.minimumDistanceBetweenCars))
          .foreach { _ =>
            nextReservationRequestAttempt = None
            context.self ! TrySendReservationRequest
          }

//        if (isTraversing) {
        import pl.edu.agh.wmazur.avs.Dimension
//          println(s"""
//               |to lane: ${reservationDetails.get.departureLane.id}
//               |velocity:     ${vehicle.velocity}
//               |acceleration: ${vehicle.acceleration}
//               |remainingDistance: ${reservationDetails.get.departureLane.geometry
//                       .difference(
//                         nextIntersectionGeometry
//                           .orElse(prevIntersectionGeometry)
//                           .get)
//                       .distance(vehicle.geometry)
//                       .geoDegrees}
//               |""".stripMargin)
//        }

        if (!hasLeavedAdmissionControlZone) {
          for {
            details <- reservationDetails
            distance <- driverGauges.distanceToPrevIntersection
            if distance > details.admissionZoneLength
            msg = ExitedControlZone(details.reservationId, context.self)
          } {
            hasLeavedAdmissionControlZone = true
            details.intersectionManagerRef ! msg
          }
        }

        if (driverGauges.distanceToPrevIntersection.exists(
              _ > currentLane.length) ||
            (spawnTime.exists(currentTime - _ > 10 * 1000) && (nextIntersectionManager
              .orElse(prevIntersectionManager)
              .isEmpty || isRetryingToMakeReservation))) {
          Behaviors.stopped
        } else {
          Behaviors.same
        }
      case reading: BasicReading =>
        val driverRef = reading.driverRef
        if (driverInFront.exists(_.ref == driverRef)) {
          driverInFront = Some {
            updatedDriverReadings(driverInFront.get, reading)
          }

          driverGauges =
            driverGauges.updateVehicleInFront(driverInFront, vehicle)
        }
        Behaviors.same

      case EmptyReading(driverRef) =>
        if (driverInFront.exists(_.ref == driverRef)) {
          driverInFront = None
          driverGauges =
            driverGauges.updateVehicleInFront(driverInFront, vehicle)
        }
        Behaviors.same
    }

  private def updatedDriverReadings(
      cachedReading: VehicleCachedReadings,
      reading: BasicReading): VehicleCachedReadings = {
    cachedReading
      .modify(_.position)
      .setTo(reading.position)
      .modify(_.velocity)
      .setTo(reading.velocity)
      .modify(_.geometry)
      .setTo(Some(reading.geometry))
      .modify(_.ref)
      .setTo(reading.driverRef)
  }

}

object OnTick {
  trait Protocol {
    case class Tick(currentTime: Timestamp)
        extends AutonomousVehicleDriver.ExtendedProtocol
  }
}
