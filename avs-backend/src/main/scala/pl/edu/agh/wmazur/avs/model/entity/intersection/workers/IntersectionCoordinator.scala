package pl.edu.agh.wmazur.avs.model.entity.intersection.workers

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import pl.edu.agh.wmazur.avs.model.entity.intersection.workers.IntersectionCoordinator.Protocol.GetMaxCrossingVelocity
import pl.edu.agh.wmazur.avs.model.entity.intersection.{
  AutonomousRoadIntersection,
  IntersectionManager
}
import pl.edu.agh.wmazur.avs.model.entity.road.{Lane, Road}
import pl.edu.agh.wmazur.avs.model.entity.utils.MathUtils
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.{Angle, Velocity}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.{
  AutonomousVehicleDriver,
  CrashTestDriver
}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.{
  Vehicle,
  VehicleGauges,
  VehicleSpec,
  VirtualVehicle
}
import pl.edu.agh.wmazur.avs.protocol.SimulationProtocol
import pl.edu.agh.wmazur.avs.simulation.TickSource
import pl.edu.agh.wmazur.avs.{Agent, Dimension}

import scala.annotation.tailrec
import scala.collection.mutable
import scala.concurrent.duration.{FiniteDuration, _}

class IntersectionCoordinator(
    val context: ActorContext[IntersectionCoordinator.Protocol],
    intersectionManagerRef: ActorRef[IntersectionManager.Protocol],
    intersection: AutonomousRoadIntersection)
    extends Agent[IntersectionCoordinator.Protocol] {

  private val roadsById = intersection.roads
    .map(road => road.id -> road)
    .toMap
  private val lanesById =
    intersection.roads
      .flatMap(_.lanes)
      .map(lane => lane.id -> lane)
      .toMap

  private val cachedTravelsalDistances
    : mutable.Map[(Lane#Id, Lane#Id), Dimension] = mutable.Map.empty
  private val cachedTurnVelocities
    : mutable.Map[VehicleSpec, mutable.Map[(Lane#Id, Lane#Id), Velocity]] =
    mutable.Map.empty

  private val lanePriorites: Map[Lane, Map[Road, List[Lane]]] = for {
    (lane, entryPoint) <- intersection.entryPoints
    roadPriorities = for {
      exitRoad <- intersection.exitRoads
      sortedExitLanes = exitRoad.lanes.sortBy(_.distanceFromPoint(entryPoint))
      roadPriorities = exitRoad -> sortedExitLanes
    } yield roadPriorities
  } yield lane -> roadPriorities.toMap

  override protected val initialBehaviour
    : Behavior[IntersectionCoordinator.Protocol] = active()

  private def active(): Behavior[IntersectionCoordinator.Protocol] =
    Behaviors.receiveMessagePartial {
      case GetMaxCrossingVelocity(replyTo, laneId, roadId, vehicleSpec) =>
        if (lanesById.keySet.contains(laneId) && roadsById.contains(roadId)) {
          val possibleVelocities: Map[Lane, Velocity] = {
            for {
              currentLane <- lanesById(laneId) :: Nil
              destianationRoad = roadsById(roadId)
              destinationLane <- lanePriorites(currentLane)(destianationRoad)

              cachedVelocitesForSpec = cachedTurnVelocities.getOrElseUpdate(
                vehicleSpec,
                mutable.Map.empty)

              maxVelocity = cachedVelocitesForSpec.getOrElseUpdate(
                (currentLane.id, destinationLane.id),
                calcMaxTurnVelocity(vehicleSpec, currentLane, destinationLane))
            } yield destinationLane -> maxVelocity
          }.toMap
//          Todo czy limitować ilość wyjściowych jezdni?
          replyTo ! AutonomousVehicleDriver.MaxCrossingVelocities(
            intersectionManagerRef,
            laneId,
            possibleVelocities)
        } else {
          replyTo ! AutonomousVehicleDriver.NoPathForLanes(laneId, roadId)
        }
        Behaviors.same
    }

  def calcMaxTurnVelocity(vehicleSpec: VehicleSpec,
                          arrivalLane: Lane,
                          departureLane: Lane): Velocity = {
    val precision = 0.2 // m/s

    @tailrec
    def iterate(minVelocity: Velocity, maxVelocity: Velocity): Velocity = {
      if (maxVelocity - minVelocity > precision) {
        val testedVelocity = (minVelocity + maxVelocity) / 2
        val isSafeToCross = isSafeToCrossIntersection(vehicleSpec = vehicleSpec,
                                                      arrivalLane = arrivalLane,
                                                      departureLane =
                                                        departureLane,
                                                      velocity = testedVelocity)

        if (isSafeToCross) {
          iterate(testedVelocity, maxVelocity)
        } else {
          iterate(minVelocity, testedVelocity)
        }
      } else {
        minVelocity
      }
    }

    val maxVelocity = List(vehicleSpec.maxVelocity,
                           arrivalLane.spec.speedLimit,
                           departureLane.spec.speedLimit).min
    iterate(0, maxVelocity)
  }

  def isSafeToCrossIntersection(vehicleSpec: VehicleSpec,
                                arrivalLane: Lane,
                                departureLane: Lane,
                                velocity: Velocity): Boolean = {
    if (velocity <= 0 || velocity > vehicleSpec.maxVelocity) {
      false
    } else {
      val travelsalDistance = cachedTravelsalDistances
        .getOrElseUpdate(
          (arrivalLane.id, departureLane.id),
          intersection.calcTravelsalDistance(arrivalLane, departureLane)
        )
      val maxTime = (travelsalDistance.meters / velocity).seconds
      val position = intersection.entryPoints(arrivalLane)
      val heading = intersection.entryHeadings(arrivalLane)
      val testDriver = CrashTestDriver(
        vehicle = VirtualVehicle(
          gauges =
            VehicleGauges(position,
                          velocity,
                          0,
                          0,
                          heading,
                          Vehicle.calcArea(position, heading, vehicleSpec)),
          spec = vehicleSpec,
          targetVelocity = 0,
          spawnTime = -1),
        arrivalLane,
        departureLane
      )

      val safeTraversalSteeringDelta: Angle = 0.008 //radians
      val safeTraversalSteeringThreshold: Angle = 0.4 //radians
      val safeTraversalHeadingThreshold: Angle = 0.35 //radians
      @tailrec
      def isSafeToCross(time: FiniteDuration,
                        testDriver: CrashTestDriver,
                        enteredIntersection: Boolean,
                        minSteeringAngle: Angle,
                        maxSteeringAngle: Angle): Boolean = {
        def timeElapsed = time <= maxTime

        def withinIntersection =
          intersection.area.relate(testDriver.vehicle.position).intersects()

        val finished = timeElapsed && (!enteredIntersection || withinIntersection)
        if (!finished) {
          val beforeMoveState = testDriver.prepareToMove()

          val afterMoveState = beforeMoveState.withVehicle(
            beforeMoveState.vehicle
              .move(TickSource.timeStepSeconds))

          val newMinSteeringAngle =
            testDriver.vehicle.steeringAngle.min(minSteeringAngle)
          val newMaxSteeringAngle =
            testDriver.vehicle.steeringAngle.max(maxSteeringAngle)

          val didEnteredIntersection = enteredIntersection || testDriver.vehicle.area
            .relate(intersection.bufferedArea)
            .intersects()

          isSafeToCross(time + TickSource.timeStep,
                        afterMoveState,
                        didEnteredIntersection,
                        newMinSteeringAngle,
                        newMaxSteeringAngle)
        } else {
          def tooBigSteeringAngleDelta: Boolean =
            -minSteeringAngle > safeTraversalSteeringDelta &&
              maxSteeringAngle > safeTraversalSteeringDelta

          this match {
            case _ if time > maxTime           => false
            case _ if tooBigSteeringAngleDelta => false
            case _ =>
              def minDistance: Double =
                (departureLane.spec.width - testDriver.vehicle.spec.width).meters / 3

              def distanceToMiddleOfLane: Double =
                departureLane.middleLine.distance(
                  testDriver.vehicle.positionAsGeometry)

              def isInMiddleOfLane = distanceToMiddleOfLane < minDistance

              def finishedSteering =
                testDriver.vehicle.steeringAngle.abs < safeTraversalSteeringThreshold

              def isHeadingRightDirection: Boolean =
                MathUtils.angleDiff(
                  testDriver.vehicle.heading,
                  intersection.exitHeading(departureLane)
                ) < safeTraversalHeadingThreshold

              isInMiddleOfLane && finishedSteering && isHeadingRightDirection
          }
        }
      }

      isSafeToCross(time = Duration.Zero,
                    testDriver = testDriver,
                    enteredIntersection = false,
                    minSteeringAngle = 0,
                    maxSteeringAngle = 0)
    }
  }

}

object IntersectionCoordinator {
  sealed trait CoordinatorProtocol

  sealed trait Protocol
      extends IntersectionManager.Protocol
      with CoordinatorProtocol
  object Protocol {
    case class GetMaxCrossingVelocity(
        replyTo: ActorRef[AutonomousVehicleDriver.Protocol],
        currentLane: Lane#Id,
        destinationRoad: Road#Id,
        vehicleSpec: VehicleSpec,
    ) extends Protocol
  }

  def init(intersectionManagerRef: ActorRef[IntersectionManager.Protocol],
           intersection: AutonomousRoadIntersection): Behavior[Protocol] =
    Behaviors.setup { ctx =>
      new IntersectionCoordinator(ctx, intersectionManagerRef, intersection)
    }

}
