package pl.edu.agh.wmazur.avs.backend.http.codec

import pl.edu.agh.wmazur.avs.model.entity.vehicle.Vehicle
import pl.edu.agh.wmazur.avs.model.state.SimulationStateUpdate
import protobuf.pl.edu.agh.wmazur.avs.model.Envelope.Message
import protobuf.pl.edu.agh.wmazur.avs.model.common.{Vector3 => ProtoVector3}
import protobuf.pl.edu.agh.wmazur.avs.model.vehicle.{Vehicle => ProtoVehicle}
import pl.edu.agh.wmazur.avs.model.entity.utils.SpatialUtils.PointUtils
import protobuf.pl.edu.agh.wmazur.avs.model.StateUpdate
import protobuf.pl.edu.agh.wmazur.avs.model.StateUpdate.{
  Created,
  Deleted,
  UpdateMeta,
  UpdateType,
  Updated
}
import scala.concurrent.duration._

object SimulationStateUpdateEncoder
    extends MessageEncoder[SimulationStateUpdate, Message.StateUpdate] {
  def vehicleEncoder(vehicle: Vehicle): ProtoVehicle = ProtoVehicle(
    id = vehicle.id.toString,
    currentPosition = {
      val coords = vehicle.position.simpleCoordinates
      Some(
        ProtoVector3
          .of(coords.x.toFloat, 0f, coords.y.toFloat))
    },
    speed = vehicle.velocity.floatValue(),
    acceleration = vehicle.acceleration.floatValue()
  )

  override protected def decodingFunction: PartialFunction[
    SimulationStateUpdate,
    SimulationStateUpdateEncoder.Out] = {
    case update =>
      val updateType = if (update.isDelta) UpdateType.Delta else UpdateType.Full
      if (updateType.isFull) {
        println(update)
      }

      StateUpdate(
        updateType = updateType,
        timestamp = update.timestamp,
      ).withCreated(
          Created(
            vehicles = update.vehicles.created.map(vehicleEncoder).toSeq,
            intersections = Nil,
            roads = Nil
          ))
        .withUpdated(
          Updated(
            vehicles = update.vehicles.updated.map(vehicleEncoder).toSeq,
            intersections = Nil,
            roads = Nil
          ))
        .withDeleted(
          Deleted(
            vehicles = update.vehicles.removed.toSeq.map(_.toString),
            intersections = Nil,
            roads = Nil
          ))
        .withMeta(UpdateMeta(
          updatesPerSecond = (1.second / update.timeDelta).toInt
        ))
  }

  override protected val toWrapper
    : SimulationStateUpdateEncoder.Out => Message.StateUpdate =
    Message.StateUpdate.apply
}
