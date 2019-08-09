package pl.edu.agh.wmazur.avs.model.entity.vehicle.driver
import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.{ActorContext, Behaviors, TimerScheduler}
import akka.actor.typed.{ActorRef, Behavior}
import org.locationtech.spatial4j.shape.Point
import pl.edu.agh.wmazur.avs.model.entity.road.RoadManager.VehicleLanesOccupation
import pl.edu.agh.wmazur.avs.model.entity.road.{Lane, Road}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{Angle, Velocity}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.AutonomousVehicleDriver.Protocol
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.protocol.{
  DriverConnectivity,
  Driving,
  OnTick,
  PreperingReservation
}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.{
  BasicVehicle,
  Vehicle,
  VehicleSpec
}
import pl.edu.agh.wmazur.avs.simulation.EntityManager.SpawnResult
import pl.edu.agh.wmazur.avs.{Agent, EntityRefsGroup}

import scala.collection.mutable

class AutonomousVehicleDriver(
    val context: ActorContext[AutonomousVehicleDriver.Protocol],
    spawnLane: Lane,
    var vehicle: BasicVehicle,
    val timers: TimerScheduler[AutonomousVehicleDriver.Protocol]
) extends Agent[Protocol]
    with VehicleDriver
    with VehiclePilot
    with DriverConnectivity
    with OnTick
    with Driving
    with PreperingReservation {

  override val occupiedLanes: mutable.Set[Lane] = mutable.Set.empty
  override var currentLane: Lane = setCurrentLane(spawnLane)

  var destination: Option[Road] = Some(currentLane.spec.road.get)

  def switchTo(behavior: Behavior[Protocol]): Behavior[Protocol] =
    behavior
      .orElse(onTickBehavior)
      .orElse(basicConnectivity)
      .orElse(drive)
      .orElse(preperingReservation)
      .orElse {
        Behaviors.receiveMessage { msg =>
          context.log.error("Unhanded message: ", { msg })
          Behaviors.same
        }
      }

  override protected val initialBehaviour: Behavior[Protocol] = switchTo(drive)

  override protected def setCurrentLane(lane: Lane): Lane = {
    val previousState = occupiedLanes.toSet
    super.setCurrentLane(lane)
    val currentState = occupiedLanes.toSet
    lane.spec.road.foreach(
      _.managerRef ! VehicleLanesOccupation(context.self,
                                            currentState,
                                            previousState))
    lane
  }

  override def withVehicle(vehicle: Vehicle): this.type = {
    vehicle match {
      case bv: BasicVehicle => this.vehicle = bv
      case _                => throw new RuntimeException("Unsupported operation")
    }
    this
  }
}

object AutonomousVehicleDriver
    extends DriverConnectivity.Protocol
    with Driving.Protocol
    with OnTick.Protocol
    with PreperingReservation.Protocol {

  type Protocol = VehicleDriver.Protocol
  trait ExtendedProtocol extends VehicleDriver.Protocol

  def init(
      id: Vehicle#Id,
      spec: VehicleSpec,
      position: Point,
      heading: Angle,
      velocity: Velocity,
      lane: Lane,
      replyTo: Option[ActorRef[SpawnResult[Vehicle, VehicleDriver.Protocol]]] =
        None): Behavior[ExtendedProtocol] = {
    Behaviors.setup[VehicleDriver.Protocol] { ctx =>
      val vehicle = BasicVehicle(driverRef = ctx.self,
                                 spec = spec,
                                 position = position,
                                 heading = heading,
                                 velocity = velocity)
      replyTo.foreach(_ ! SpawnResult(vehicle, ctx.self.unsafeUpcast))

      ctx.system.receptionist ! Receptionist.register(EntityRefsGroup.driver,
                                                      ctx.self)

      Behaviors.withTimers { timers =>
        new AutonomousVehicleDriver(context = ctx,
                                    spawnLane = lane,
                                    vehicle = vehicle,
                                    timers)
      }
    }
  }.narrow

}
