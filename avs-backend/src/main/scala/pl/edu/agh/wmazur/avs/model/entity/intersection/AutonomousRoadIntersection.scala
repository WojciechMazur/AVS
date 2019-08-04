package pl.edu.agh.wmazur.avs.model.entity.intersection
import akka.actor.typed.ActorRef
import org.locationtech.jts.geom.{Geometry, LineString, Polygon}
import org.locationtech.jts.operation.union.CascadedPolygonUnion
import org.locationtech.spatial4j.context.jts.JtsSpatialContext
import org.locationtech.spatial4j.shape.{Point, Shape}
import pl.edu.agh.wmazur.avs.Dimension
import pl.edu.agh.wmazur.avs.model.entity.EntitySettings
import pl.edu.agh.wmazur.avs.model.entity.intersection.AutonomousRoadIntersection.IntersectionAreaSegments
import pl.edu.agh.wmazur.avs.model.entity.road.{Lane, Road}
import pl.edu.agh.wmazur.avs.model.entity.utils.SpatialUtils
import pl.edu.agh.wmazur.avs.model.entity.vehicle.Vehicle.Vin
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.Angle

case class AutonomousRoadIntersection(
    id: Vin,
    roads: Iterable[Road],
    manager: ActorRef[AutonomousIntersectionManager.Protocol]
) extends Intersection {
  import pl.edu.agh.wmazur.avs.model.entity.utils.SpatialUtils._

  import scala.collection.breakOut

  val lanes: Iterable[Lane] = roads.flatMap(_.lanes)
  private val iaSegments: AutonomousRoadIntersection.IntersectionAreaSegments =
    extractAreaSegments(roads)
  lazy val centroid: Point = AutonomousRoadIntersection.shapeFactory
    .makeShapeFromGeometry(iaSegments.geometry.get.getCentroid)
    .asInstanceOf[Point]

  lazy val wayPoints: Vector[Point] =
    (iaSegments.entryPoints.values ++ iaSegments.exitPoints.values)
      .map { point =>
        point -> point.angle(centroid)
      }
      .toVector
      .sortBy { case (_, angle) => angle }
      .map { case (point, _) => point }

  val roadAtLane: Map[Lane, Road] = roads.flatMap { road =>
    road.lanes.map(_ -> road)
  }(breakOut)

  override val entryRoads: Set[Road] =
    iaSegments.entryPoints.keySet.map(roadAtLane)
  override val exitRoads: Set[Road] =
    iaSegments.exitPoints.keySet.map(roadAtLane)

  lazy val entryPoints: Map[Lane, Point] = iaSegments.entryPoints
  lazy val exitPoints: Map[Lane, Point] = iaSegments.exitPoints
  lazy val entryHeadings: Map[Lane, Angle] = iaSegments.entryHeadings
  lazy val exitHeading: Map[Lane, Angle] = iaSegments.exitHeadings

//  override def intersectionManager: IntersectionManager = ???
  override def calcTravelsalDistance(arrivalLane: Lane,
                                     departureLane: Lane): Dimension = {

    def lineStringLength(shape: Shape): Dimension = {
      shape match {
        case line: LineString => line.getLength.fromGeoDegrees
        case _                => sys.error("This should not happend")
      }
    }

    if (arrivalLane == departureLane) {
      (
        entryPoints(arrivalLane) distance exitPoints(departureLane)
      ).fromGeoDegrees
    } else {
      val arrivalLaneEnd = if (isExitedBy(arrivalLane)) {
        exitPoints(arrivalLane)
      } else {
        arrivalLane.exitPoint
      }

      val departureLaneStart = if (isEnteredBy(departureLane)) {
        entryPoints(departureLane)
      } else {
        departureLane.entryPoint
      }

      val arrivalLaneIntersectionSegment =
        LineFactory(entryPoints(arrivalLane), arrivalLaneEnd)
      val departureLineIntersectionSegment =
        LineFactory(departureLaneStart, exitPoints(departureLane))

      val segmentsIntersects = arrivalLaneIntersectionSegment
        .relate(departureLineIntersectionSegment)
        .intersects()

      val segments = if (segmentsIntersects) {
        val arrivalSegmentGeometry =
          shapeFactory.getGeometryFrom(arrivalLaneIntersectionSegment)
        val departureSegmentGeometry =
          shapeFactory.getGeometryFrom(departureLineIntersectionSegment)

        val intersectionPoint =
          arrivalSegmentGeometry.intersection(departureSegmentGeometry) match {
            case point: Point => point
            case _            => sys.error("This should not happend")
          }

        List(
          LineFactory(entryPoints(arrivalLane), intersectionPoint),
          LineFactory(intersectionPoint, exitPoints(departureLane))
        )

      } else {
        List(
          arrivalLaneIntersectionSegment,
          departureLineIntersectionSegment
        )
      }

      segments
        .map(lineStringLength)
        .reduce(_ + _)
    }
  }

  override lazy val area: Shape =
    AutonomousRoadIntersection.shapeFactory.makeShapeFromGeometry {
      iaSegments.geometry.get
    }

  override lazy val position: Point = centroid

  private def findIntersectionGeometry(roads: Iterable[Road]): Geometry = {
    val (_, geometry) =
      roads
        .zip(roads.tail)
        .foldLeft((Set.empty[(Road, Road)], Option.empty[Geometry])) {
          case ((collectedRoads, intersectionGeometry), pair @ (lr, rr))
              if !collectedRoads.contains((rr, lr)) =>
//                !lr.oppositeRoad.contains(rr) =>

            val lrGeometry =
              AutonomousRoadIntersection.shapeFactory
                .getGeometryFrom(lr.area)

            val rrGeometry =
              AutonomousRoadIntersection.shapeFactory
                .getGeometryFrom(rr.area)

            val roadsIntersection = lrGeometry.intersection(rrGeometry)

            val newIntersectionGeometry = intersectionGeometry match {
              case Some(geo) => geo.union(roadsIntersection)
              case _         => roadsIntersection
            }

            (collectedRoads + pair, Some(newIntersectionGeometry))
        }
    geometry.get
  }

  private def extractIntersectionPointDistances(
      geometrySegments: Iterable[LineString],
      roads: Iterable[Road]): (Map[Lane, Angle], Map[Lane, Angle]) = {

    val distances: Map[Lane, Iterable[Angle]] = {
      for {
        road <- roads
        lane <- road.lanes
        distances = for {
          segment <- geometrySegments
          point <- lane.centerIntersectionPoint(segment)
        } yield lane.normalizedDistanceAlongLane(point)
      } yield lane -> distances
    }.toMap
      .filter {
        case (_, values) => values.nonEmpty
      }

    val minDistances: Map[Lane, Double] =
      distances.mapValues(_.min)
    val maxDistances: Map[Lane, Double] =
      distances.mapValues(_.max)

    (minDistances, maxDistances)
  }

  override type Self = AutonomousRoadIntersection
  def entitySettings: EntitySettings[AutonomousRoadIntersection] =
    AutonomousRoadIntersection

  private def extractAreaSegments(
      roads: Iterable[Road]): IntersectionAreaSegments = {
    val intersectionGeometry = findIntersectionGeometry(roads)
    val geometrySegments = intersectionGeometry.getCoordinates
      .sliding(2, 1)
      .map {
        case Array(c1, c2) => LineGeometryFactory(c1, c2)
      }
      .toList

    val intersectionShape =
      AutonomousRoadIntersection.shapeFactory.makeShapeFromGeometry(
        intersectionGeometry)
    val (minDistanceFromLanes, maxDistanceFromLanes) =
      extractIntersectionPointDistances(geometrySegments, roads)
    val intersectingLanes = minDistanceFromLanes.keySet

    intersectingLanes.foldLeft(
      IntersectionAreaSegments(geometry = Some(intersectionGeometry))) {
      case (IntersectionAreaSegments(optGeometry,
                                     inletPoints,
                                     outletPoints,
                                     inletHeadings,
                                     outletHeadings),
            lane) =>
        //TODO czy na pewno powinno być zależne od długości pasa?
        val expansionOffset = AutonomousRoadIntersection.expansionDistance.meters / lane.length.meters
        val (entryFraction, newEntryPoints, newEntryHeadings) =
          if (intersectionShape.relate(lane.entryPoint).intersects()) {
            (0d, inletPoints, inletHeadings)
          } else {
            val fraction = (minDistanceFromLanes(lane) - expansionOffset) max 0d
            (
              fraction,
              inletPoints + (lane -> lane.pointAtNormalizedDistance(fraction)),
              inletHeadings + (lane -> lane.headingAtNormalizedDistance(
                fraction))
            )
          }
        val (exitFraction, newExitPoints, newExitHeadings) =
          if (intersectionShape.relate(lane.exitPoint).intersects()) {
            (1d, outletPoints, outletHeadings)
          } else {
            val fraction = (maxDistanceFromLanes(lane) + expansionOffset) min 1d
            (
              fraction,
              outletPoints + (lane -> lane.pointAtNormalizedDistance(fraction)),
              outletHeadings + (lane -> lane.headingAtNormalizedDistance(
                fraction))
            )
          }
        val geometryFragment =
          lane.getGeometryFraction(entryFraction, exitFraction)
        //TODO check if really needed
        val filledGeometryFragment = geometryFragment
//          .union {
//          geometryFragment.symDifference(geometryFragment)
//        }
        val newGeometry = optGeometry match {
          case Some(geo) => geo.union(filledGeometryFragment)
          case _         => filledGeometryFragment
        }

        IntersectionAreaSegments(Some(newGeometry),
                                 newEntryPoints,
                                 newExitPoints,
                                 newEntryHeadings,
                                 newExitHeadings)
    }
  }
}

object AutonomousRoadIntersection
    extends EntitySettings[AutonomousRoadIntersection] {
  //Offset for additional space at intersection, e.q crosswalks
  val expansionDistance: Dimension = 2.0.fromMeters
  private val shapeFactory = SpatialUtils.shapeFactory

//  def apply(roads: Iterable[Road]): RoadIntersection =
//    new RoadIntersection(Intersection.nextId, roads)

  private case class IntersectionAreaSegments(
      geometry: Option[Geometry] = None,
      entryPoints: Map[Lane, Point] = Map.empty,
      exitPoints: Map[Lane, Point] = Map.empty,
      entryHeadings: Map[Lane, Angle] = Map.empty,
      exitHeadings: Map[Lane, Angle] = Map.empty)
}
