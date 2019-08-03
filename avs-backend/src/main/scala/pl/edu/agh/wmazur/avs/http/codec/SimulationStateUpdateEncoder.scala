package pl.edu.agh.wmazur.avs.http.codec

import org.locationtech.jts.geom.Coordinate
import org.locationtech.spatial4j.distance.DistanceUtils
import org.locationtech.spatial4j.shape.{Point, Shape}
import pl.edu.agh.wmazur.avs.model.entity.intersection.Intersection
import pl.edu.agh.wmazur.avs.model.entity.road.Road
import pl.edu.agh.wmazur.avs.model.entity.utils.SpatialUtils
import pl.edu.agh.wmazur.avs.model.entity.utils.SpatialUtils.{PointUtils, _}
import pl.edu.agh.wmazur.avs.model.entity.vehicle.Vehicle
import pl.edu.agh.wmazur.avs.model.state.SimulationStateUpdate
import protobuf.pl.edu.agh.wmazur.avs.model.Envelope.Message
import protobuf.pl.edu.agh.wmazur.avs.model.StateUpdate
import protobuf.pl.edu.agh.wmazur.avs.model.StateUpdate.{
  Created,
  Deleted,
  UpdateMeta,
  UpdateType,
  Updated
}
import protobuf.pl.edu.agh.wmazur.avs.model.common.{
  Geometry => ProtoGeometry,
  Vector3 => ProtoVector3
}
import protobuf.pl.edu.agh.wmazur.avs.model.intersection.{
  Intersection => ProtoIntersection
}
import protobuf.pl.edu.agh.wmazur.avs.model.road.{
  CollectPoint => ProtoCollectPoint,
  Lane => ProtoLane,
  Road => ProtoRoad,
  SpawnPoint => ProtoSpawnPoint
}
import protobuf.pl.edu.agh.wmazur.avs.model.vehicle.{
  Vehicle => ProtoVehicle,
  VehicleSpec => ProtoVehicleSpec
}

import scala.concurrent.duration._

object SimulationStateUpdateEncoder
    extends MessageEncoder[SimulationStateUpdate, Message.StateUpdate] {

  private def geoDegresToMeters(value: Double): Double = {
    value * DistanceUtils.DEG_TO_KM * 1000
  }
  def pointEncoder(point: Point): ProtoVector3 = {
    val x = geoDegresToMeters(point.x).toFloat
    val y = geoDegresToMeters(point.y).toFloat
    ProtoVector3.of(
      x = x,
      y = 0f,
      z = y
    )
  }

  def coordinateEncoder(point: Coordinate): ProtoVector3 = {
    val x = geoDegresToMeters(point.x).toFloat
    val y = geoDegresToMeters(point.y).toFloat
    ProtoVector3.of(
      x = x,
      y = 0f,
      z = y
    )
  }

  def geometryEncoder(shape: Shape): ProtoGeometry = {
    val geometry = SpatialUtils.shapeFactory.getGeometryFrom(shape)

    val shapes = 0
      .until(geometry.getNumGeometries)
      .map(geometry.getGeometryN)
      .map(_.getCoordinates.map(coordinateEncoder))
      .map(indices => ProtoGeometry.Shape.of(indices))

    val position = for {
      point <- Some(shape.getCenter)
    } yield pointEncoder(point)

    ProtoGeometry.of(position, shapes)
  }

  def vehicleEncoder(vehicle: Vehicle): ProtoVehicle = {
    val spec = ProtoVehicleSpec(
      width = vehicle.spec.width.meters.toFloat,
      length = vehicle.spec.length.meters.toFloat,
      height = vehicle.spec.height.meters.toFloat,
      geometry = Some(geometryEncoder(vehicle.area))
    )
    ProtoVehicle(
      id = vehicle.id.toString,
      currentPosition = {
        val coords = vehicle.position.simpleCoordinates
        Some(
          ProtoVector3
            .of(coords.x.toFloat, 0f, coords.y.toFloat))
      },
      heading = vehicle.heading.toFloat,
      speed = vehicle.velocity.floatValue(),
      acceleration = vehicle.acceleration.floatValue(),
      spec = Some(spec)
    )
  }
  def roadEncoder(road: Road): ProtoRoad = {
    val lanes = road.lanes.map { lane =>
      val spawnPoint = for {
        spawnPoint <- lane.spawnPoint
        geometry = geometryEncoder(spawnPoint.spawnArea)
      } yield ProtoSpawnPoint.of(Some(geometry))

      val collectPoint = for {
        collectPoint <- lane.collectorPoint
        geometry = geometryEncoder(collectPoint.collectArea)
      } yield ProtoCollectPoint.of(Some(geometry))

      ProtoLane.of(
        id = lane.id.toString,
        geometry = Some(geometryEncoder(lane.area)),
        entryPoint = Some(pointEncoder(lane.entryPoint)),
        exitPoint = Some(pointEncoder(lane.exitPoint)),
        spawnPoint = spawnPoint,
        collectPoint = collectPoint
      )
    }

    ProtoRoad.of(id = road.id.toString,
                 lanes = lanes,
                 geometry = Some(geometryEncoder(road.area)))
  }

  def intersectionEncoder(intersection: Intersection): ProtoIntersection = {
    val entryPoints = intersection.entryPoints.values.map(pointEncoder).toSeq
    val exitPoints = intersection.exitPoints.values.map(pointEncoder).toSeq

    ProtoIntersection.of(
      id = intersection.id.toString,
      geometry = Some(geometryEncoder(intersection.area)),
      entryPoints = entryPoints,
      exitPoints = exitPoints
    )
  }

  override protected def decodingFunction: PartialFunction[
    SimulationStateUpdate,
    SimulationStateUpdateEncoder.Out] = {
    case update =>
      val updateType = if (update.isDelta) UpdateType.Delta else UpdateType.Full

      StateUpdate(
        updateType = updateType,
        timestamp = update.timestamp,
      ).withCreated(Created(
          vehicles = update.vehicles.created.map(vehicleEncoder).toSeq,
          intersections =
            update.intersections.created.map(intersectionEncoder).toSeq,
          roads = update.roads.created.map(roadEncoder).toSeq
        ))
        .withUpdated(Updated(
          vehicles = update.vehicles.updated.map(vehicleEncoder).toSeq,
          intersections =
            update.intersections.updated.map(intersectionEncoder).toSeq,
          roads = update.roads.updated.map(roadEncoder).toSeq
        ))
        .withDeleted(Deleted(
          vehicles = update.vehicles.removed.toSeq.map(_.toString),
          intersections = update.intersections.removed.map(_.toString).toSeq,
          roads = update.roads.removed.toSeq.map(_.toString)
        ))
        .withMeta(UpdateMeta(
          updatesPerSecond = (1.second / update.timeDelta).toInt
        ))
  }

  override protected val toWrapper
    : SimulationStateUpdateEncoder.Out => Message.StateUpdate =
    Message.StateUpdate.apply
}
