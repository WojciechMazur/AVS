package pl.edu.agh.wmazur.avs.model.entity.intersection.extension

import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.Behaviors
import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.intersection.IntersectionManager.Protocol.IntersectionCrossingRequest.Proposal
import pl.edu.agh.wmazur.avs.model.entity.intersection.IntersectionManager.Protocol.{
  CancelReservation,
  ExitedControlZone,
  IntersectionCrossingRequest,
  ReservationCompleted
}
import pl.edu.agh.wmazur.avs.model.entity.intersection.extension.ReservationSystem.ProposalFilter.{
  AcceptedResult,
  RejectedResult
}
import pl.edu.agh.wmazur.avs.model.entity.intersection.reservation.AdmissionControlZoneManager.AdmissionQuery
import pl.edu.agh.wmazur.avs.model.entity.intersection.reservation.{
  AdmissionControlZone,
  AdmissionControlZoneManager,
  GridReservationManager
}
import pl.edu.agh.wmazur.avs.model.entity.intersection.reservation.ReservationArray.{
  ReservationId,
  Timestamp
}
import pl.edu.agh.wmazur.avs.model.entity.intersection.{
  AutonomousIntersectionManager,
  IntersectionManager
}
import pl.edu.agh.wmazur.avs.model.entity.road.Lane
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.VehicleDriver.Protocol.ReservationRejected.Reason
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.VehicleDriver.Protocol.{
  ReservationConfirmed,
  ReservationDetails,
  ReservationRejected
}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.{
  VehicleDriver,
  VehiclePilot
}

import scala.collection.mutable
import scala.concurrent.duration._

trait ReservationSystem {
  self: AutonomousIntersectionManager =>
  import ReservationSystem._

  type DriverRef = ActorRef[VehicleDriver.Protocol]

  val mainReservationManager = GridReservationManager(
    gridManagerConfig,
    intersection
  )
  val admissionControlZonesManagers: Map[Lane#Id, AdmissionControlZoneManager] =
    intersection.exitPoints.keySet.map { lane =>
      val newZone = AdmissionControlZoneManager(
        AdmissionControlZone(admissionControlZoneLength)
      )
      lane.id -> newZone
    }.toMap

  val reservationRegistry: mutable.Map[ReservationId, ReservationRecord] =
    mutable.Map.empty
  val driverReservation: mutable.Map[DriverRef, ReservationId] =
    mutable.Map.empty
  val rejectedDrivers: mutable.Set[DriverRef] = mutable.Set.empty

  def standardProposalValidity(proposal: Proposal,
                               futureReservationControl: Boolean = true)
    : ProposalFilter.ProposalFilterResult = {
    // format: off
    def isArrivalTooLate = proposal.arrivalTime < currentTime
    def isTooEarly: Boolean = proposal.arrivalTime + maximumFutureReservationTime.toMillis < currentTime
    // format: on
    None match {
      case _ if isArrivalTooLate =>
        RejectedResult(proposal, Reason.ArrivalTimeTooLate)
      case _ if futureReservationControl && isTooEarly =>
        RejectedResult(proposal, Reason.TooEarly)
      case _ => AcceptedResult(proposal)
    }
  }

  /**
    * Processed messagess if other then Request
    */
  val reservationsManagement: Behaviors.Receive[IntersectionManager.Protocol] =
    Behaviors.receiveMessagePartial {
      case CancelReservation(reservationId, driverRef) =>
        reservationRegistry
          .get(reservationId)
          .filter(_.driverRef == driverRef)
          .foreach { rr =>
            mainReservationManager.cancel(reservationId)
            admissionControlZonesManagers(rr.departureLaneId).cancel(driverRef)
            reservationRegistry.remove(reservationId)
            driverReservation.remove(driverRef)
          }
        //Todo reply to vehicle
        Behaviors.same

      case ReservationCompleted(driverRef, reservationId) =>
        reservationRegistry
          .get(reservationId)
          .filter(_.driverRef == driverRef)
          .foreach { _ =>
            //Remove from registry when exited control zone
          }
        Behaviors.same
      case ExitedControlZone(reservationId, driverRef) =>
        reservationRegistry
          .get(reservationId)
          .filter(_.driverRef == driverRef)
          .foreach { rr =>
            admissionControlZonesManagers(rr.departureLaneId).retain(driverRef)
            reservationRegistry.remove(reservationId)
            driverReservation.remove(driverRef)
          }
        Behaviors.same
    }

  def calcReservationParamaters(
      request: IntersectionCrossingRequest,
      proposals: List[Proposal]): Option[ReservationParameters] = {

    def findValidProposal(
        remainingProposals: List[Proposal]): Option[ReservationParameters] = {
      remainingProposals.headOption.flatMap {
        case proposal @ Proposal(arrivalLaneId,
                                 departureLaneId,
                                 arrivalTime,
                                 arrivalVelocity,
                                 maxTurnVelocity,
                                 _) =>
          val query = GridReservationManager.ReservationQuery(
            driverRef = request.driverRef,
            arrivalTime = arrivalTime,
            arrivalVelocity = arrivalVelocity,
            maxTurnVelocity = maxTurnVelocity,
            arrivalLaneId = arrivalLaneId,
            departureLaneId = departureLaneId,
            vehicleSpec = request.spec,
            isAccelerating = true
          )

          mainReservationManager
            .scheduleTrajectory(query)
            .flatMap {
              case schedule @ GridReservationManager.ReservationSchedule(
                    driverRef,
                    exitTime,
                    _,
                    _,
                    _) =>
                for {
                  admissionManager <- admissionControlZonesManagers.get(
                    departureLaneId)
                  stopDistance = VehiclePilot.calcStoppingDistance(
                    schedule.exitVelocity,
                    request.spec.maxDeceleration)
                  admissionQuery = AdmissionQuery(driverRef,
                                                  query.vehicleSpec.length,
                                                  stopDistance)
                  admissionPlan <- admissionManager.query(admissionQuery)
                } yield
                  ReservationParameters(driverRef,
                                        proposal,
                                        schedule,
                                        exitTime,
                                        departureLaneId,
                                        admissionPlan)
            }
            .orElse(findValidProposal(remainingProposals.tail))
      }
    }
    findValidProposal(proposals)
  }

  def buildReservationAcceptance(
      requestId: Long,
      params: ReservationParameters): Option[ReservationConfirmed] = {
    val reservationId = mainReservationManager.accept(params.gridSchedule)
    val admissionId = admissionControlZonesManagers
      .get(params.admissionControlZoneId)
      .flatMap(_.accept(params.admissionControlZonePlan))

    (reservationId, admissionId) match {
      case (Some(rId), Some(aId)) =>
        val details = ReservationDetails(
          rId,
          aId,
          intersectionManagerRef = context.self,
          arrivalTime = params.successfulProposal.arrivalTime,
          safetyBufferBefore = ReservationSystem.earlyArrivalThreshold,
          safetyBufferAfter = ReservationSystem.lateArrivalThreshold,
          arrivalVelocity = params.successfulProposal.arrivalVelocity,
          arrivalLaneId = params.successfulProposal.arrivalLaneId,
          departureLaneId = params.successfulProposal.departureLaneId,
          accelerationProfile = params.gridSchedule.accelerationProfile,
        )

        val reservationRecord = ReservationRecord(
          params.driverRef,
          params.successfulProposal.departureLaneId)

        reservationRegistry.update(rId, reservationRecord)
        driverReservation.update(params.driverRef, rId)

        Some {
          ReservationConfirmed(
            rId,
            requestId,
            details
          )
        }
      case _ =>
        reservationId.foreach(mainReservationManager.cancel)
        for {
          _ <- admissionId
          admissionZoneManager <- admissionControlZonesManagers.get(
            params.admissionControlZoneId)
        } {
          admissionZoneManager.cancel(params.driverRef)
        }

        None
    }
  }

  def hasReservation(driverRef: DriverRef): Boolean = {
    driverReservation.contains(driverRef)
  }

  def hasBeenRejected(driverRef: DriverRef): Boolean = {
    rejectedDrivers.contains(driverRef)
  }

  case class ReservationRecord(driverRef: DriverRef, departureLaneId: Lane#Id)
  case class ReservationParameters(
      driverRef: DriverRef,
      successfulProposal: Proposal,
      gridSchedule: GridReservationManager.ReservationSchedule,
      exitTime: Timestamp,
      admissionControlZoneId: Lane#Id,
      admissionControlZonePlan: AdmissionControlZoneManager.AdmissionPlan
  )
}

object ReservationSystem {
  object ProposalFilter {
    sealed trait ProposalFilterResult {
      def proposal: Proposal
      def isRejected: Boolean
    }
    case class RejectedResult(proposal: Proposal,
                              resaon: ReservationRejected.Reason)
        extends ProposalFilterResult {
      override val isRejected: Boolean = true
    }
    case class AcceptedResult(proposal: Proposal) extends ProposalFilterResult {
      override val isRejected: Boolean = false
    }
  }

  val maximumFutureReservationTime: FiniteDuration = 10.seconds
  val defaultACZSize: Dimension = 40.asMeters
  val admissionControlZoneLength: Dimension = 40.meters
  val earlyArrivalThreshold: FiniteDuration = 0.05.seconds
  val lateArrivalThreshold: FiniteDuration = 0.05.seconds
}
