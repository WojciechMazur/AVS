package pl.edu.agh.wmazur.avs.model.entity.intersection.workers.coordinator

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import pl.edu.agh.wmazur.avs.model.entity.intersection.workers.coordinator.IntersectionCoordinator.Protocol.GetMaxCrossingVelocity
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
import pl.edu.agh.wmazur.avs.model.entity.vehicle.{BasicVehicle, VehicleSpec}
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

  private def active(): Behavior[IntersectionCoordinator.Protocol] =
    Behaviors.receiveMessagePartial {
      case GetMaxCrossingVelocity(replyTo,
                                  laneId,
                                  roadId,
                                  vehicle,
                                  currentTime) =>
        if (lanesById.keySet.contains(laneId) && roadsById.contains(roadId)) {
          val possibleVelocities: Map[Lane, Velocity] = {
            for {
              currentLane <- lanesById(laneId) :: Nil
              destianationRoad = roadsById(roadId)
              destinationLane <- lanePriorites(currentLane)(destianationRoad)

              cachedVelocitesForSpec = cachedTurnVelocities.getOrElseUpdate(
                vehicle.spec,
                mutable.Map.empty)

              maxVelocity = cachedVelocitesForSpec.getOrElseUpdate(
                (currentLane.id, destinationLane.id),
                calcMaxTurnVelocity(vehicle, currentLane, destinationLane))
            } yield destinationLane -> maxVelocity
          }.toMap
          //Todo czy limitować ilość wyjściowych jezdni?

        } else {
          replyTo ! AutonomousVehicleDriver.InvalidProposalParameters(laneId,
                                                                      roadId)
        }

        Behaviors.same
    }

  override protected val initialBehaviour
    : Behavior[IntersectionCoordinator.Protocol] = active()

  def calcMaxTurnVelocity(vehicle: BasicVehicle,
                          arrivalLane: Lane,
                          departureLane: Lane): Velocity = {
    val precision = 0.2 // m/s

    @tailrec
    def iterate(minVelocity: Velocity, maxVelocity: Velocity): Velocity = {
      if (maxVelocity - minVelocity < precision) {
        val testedVelocity = (minVelocity + maxVelocity) / 2
        val isSafeToCross = isSafeToCrossIntersection(vehicle = vehicle,
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

    val maxVelocity = List(vehicle.spec.maxVelocity,
                           arrivalLane.spec.speedLimit,
                           departureLane.spec.speedLimit).min
    iterate(0, maxVelocity)
  }

  def isSafeToCrossIntersection(vehicle: BasicVehicle,
                                arrivalLane: Lane,
                                departureLane: Lane,
                                velocity: Velocity): Boolean = {
    if (velocity <= 0 || velocity > vehicle.spec.maxVelocity) {
      false
    } else {
      val travelsalDistance = cachedTravelsalDistances
        .getOrElseUpdate(
          (arrivalLane.id, departureLane.id),
          intersection.calcTravelsalDistance(arrivalLane, departureLane)
        )
      val maxTime = (travelsalDistance.meters / velocity).seconds
      val testDriver = CrashTestDriver(
        vehicle = vehicle
          .withPosition(intersection.entryPoints(arrivalLane))
          .withHeading(intersection.entryHeadings(arrivalLane))
          .withVelocity(velocity)
          .withAcceleration(0)
          .withSteeringAngle(0)
          .withTargetVelocity(0),
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
          intersection.area.relate(vehicle.position).intersects()
        //        def

        val finished = timeElapsed && (!enteredIntersection || withinIntersection)
        if (!finished) {
          val beforeMoveState = testDriver.prepareToMove()

          val afterMoveState = beforeMoveState.withVehicle(
            beforeMoveState.vehicle
              .move(TickSource.timeStepSeconds))

          val newMinSteeringAngle = vehicle.steeringAngle.min(minSteeringAngle)
          val newMaxSteeringAngle = vehicle.steeringAngle.max(maxSteeringAngle)

          val didEnteredIntersection = enteredIntersection || vehicle.area
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
  sealed trait Protocol extends SimulationProtocol
  object Protocol {
    case class GetMaxCrossingVelocity(
        replyTo: ActorRef[AutonomousVehicleDriver.Protocol],
        currentLane: Lane#Id,
        destinationRoad: Road#Id,
        vehicleSpec: BasicVehicle,
    ) extends Protocol
  }

  def init(intersectionManagerRef: ActorRef[IntersectionManager.Protocol],
           intersection: AutonomousRoadIntersection): Behavior[Protocol] =
    Behaviors.setup { ctx =>
      new IntersectionCoordinator(ctx, intersectionManagerRef, intersection)
    }

}
