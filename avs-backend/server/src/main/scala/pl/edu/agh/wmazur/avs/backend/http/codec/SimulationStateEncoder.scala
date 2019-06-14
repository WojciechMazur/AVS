package pl.edu.agh.wmazur.avs.backend.http.codec

import pl.agh.edu.agh.wmazur.avs.model.SimulationState
import pl.agh.edu.agh.wmazur.avs.model.entity.vehicle.DefaultVehicle
import protobuf.pl.agh.edu.agh.wmazur.avs.model.vehicle.{
  Vehicle => ProtoVehicle
}
import protobuf.pl.agh.edu.agh.wmazur.avs.model.Envelope.Message
import protobuf.pl.agh.edu.agh.wmazur.avs.model.StateUpdate
import protobuf.pl.agh.edu.agh.wmazur.avs.model.StateUpdate.UpdateType

object SimulationStateEncoder
    extends MessageEncoder[SimulationState, Message.StateUpdate] {
  override protected def decodingFunction
    : PartialFunction[SimulationState, SimulationStateEncoder.Out] = {
    case SimulationState(totalTicks,
                         tickDelta,
                         vehicles,
                         roads,
                         intersections) =>
      val endocedVehicles = vehicles.map {
        case (_, v) =>
          ProtoVehicle(
            id = v.id,
            currentPosition = Some(v.position),
            speed = v.speed,
            acceleration = v.acceleration
          )
      }.toSeq

      StateUpdate(
        updateType = UpdateType.Delta,
        timestamp = totalTicks,
        vehicles = endocedVehicles,
        intersections = Nil,
        roads = Nil
      )
  }

  override protected val toWrapper
    : SimulationStateEncoder.Out => Message.StateUpdate =
    Message.StateUpdate.apply
}
