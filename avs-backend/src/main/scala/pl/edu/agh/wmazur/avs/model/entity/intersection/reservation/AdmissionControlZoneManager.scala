package pl.edu.agh.wmazur.avs.model.entity.intersection.reservation

import akka.actor.typed.ActorRef
import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.intersection.reservation.AdmissionControlZoneManager.{
  AdmissionPlan,
  AdmissionQuery
}
import pl.edu.agh.wmazur.avs.model.entity.utils.IdProvider
import pl.edu.agh.wmazur.avs.model.entity.vehicle.driver.VehicleDriver

case class AdmissionControlZoneManager(
    private var admissionControlZone: AdmissionControlZone) {
  def controlledDistance: Dimension = admissionControlZone.controlledDistance

  def cancel(driverRef: ActorRef[VehicleDriver.Protocol]): Unit = {
    admissionControlZone = admissionControlZone.cancel(driverRef)
  }

  def retain(driverRef: ActorRef[VehicleDriver.Protocol]): Unit = {
    admissionControlZone = admissionControlZone.retain(driverRef)
  }

  def query(query: AdmissionQuery): Option[AdmissionPlan] = {
    if (admissionControlZone.isAdmissible(query.driverRef,
                                          query.vehicleLength,
                                          query.stopDistance)) {
      Some(
        AdmissionPlan(query.driverRef, query.vehicleLength, query.stopDistance))
    } else {
//      System.err.println(
//        f"Admission control zone rejected entry for ${query.driverRef}." +
//          f" Area occupied in ${admissionControlZone.currentSize.asMeters / admissionControlZone.controlledDistance.asMeters * 100}%3.2f" + "%" +
//          f" Remaining distance ${(admissionControlZone.controlledDistance - admissionControlZone.currentSize).asMeters}%3.2fm" +
//          f" Needed distance ${query.stopDistance.asMeters}%3.2fm")
      None
    }
  }

  def accept(admissionPlan: AdmissionPlan): Option[Long] = {
    val admissionId = AdmissionControlZoneManager.admisionIdProvider.nextId
    if (admissionControlZone.currentSize + admissionPlan.vehicleLength + admissionPlan.stopDistance <= admissionControlZone.controlledDistance) {

      admissionControlZone = admissionControlZone.admit(
        admissionPlan.driverRef,
        admissionPlan.vehicleLength,
        admissionPlan.stopDistance)
      Some(admissionId)
    } else {
      None
    }
  }

}

object AdmissionControlZoneManager {
  val admisionIdProvider: IdProvider[AdmissionControlZone] =
    new IdProvider[AdmissionControlZone] {}
  case class AdmissionQuery(
      driverRef: ActorRef[VehicleDriver.Protocol],
      vehicleLength: Dimension,
      stopDistance: Dimension
  )
  case class AdmissionPlan(driverRef: ActorRef[VehicleDriver.Protocol],
                           vehicleLength: Dimension,
                           stopDistance: Dimension)
}
