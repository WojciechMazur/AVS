package pl.edu.agh.wmazur.avs.model.entity.intersection
import akka.actor.typed.ActorRef
import akka.actor.typed.receptionist.Receptionist
import org.locationtech.jts.geom.{Geometry, LineString}
import org.locationtech.spatial4j.shape.{Point, Shape}
import pl.edu.agh.wmazur.avs.{Dimension, Main}
import pl.edu.agh.wmazur.avs.model.entity.EntitySettings
import pl.edu.agh.wmazur.avs.model.entity.intersection.RoadIntersection.IntersectionAreaSegments
import pl.edu.agh.wmazur.avs.model.entity.road.{Lane, Road, RoadManager}
import pl.edu.agh.wmazur.avs.model.entity.utils.SpatialUtils
import pl.edu.agh.wmazur.avs.model.entity.vehicle.Vehicle.Vin
import pl.edu.agh.wmazur.avs.model.entity.vehicle.VehicleSpec.Angle

case class RoadIntersection(
    id: Vin,
    roads: Iterable[Road],
    manager: ActorRef[RoadManager.Protocol]
) extends Intersection {
  import pl.edu.agh.wmazur.avs.model.entity.utils.SpatialUtils._

  import scala.collection.breakOut

  val lanes: Iterable[Lane] = roads.flatMap(_.lanes)
  private val iaSegments: RoadIntersection.IntersectionAreaSegments =
    extractAreaSegments(roads)
  lazy val centroid: Point = RoadIntersection.shapeFactory
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

  override lazy val area: Shape =
    RoadIntersection.shapeFactory.makeShapeFromGeometry {
      iaSegments.geometry.get.union {
        shapeFactory.getGeometryFrom {
          PolygonFactory(
            wayPoints,
            centroid
          )
        }
      }
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
              RoadIntersection.shapeFactory.getGeometryFrom(lr.area)
            val rrGeometry =
              RoadIntersection.shapeFactory.getGeometryFrom(rr.area)

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
      roads: Iterable[Road]) = {
    geometrySegments
      .flatMap { segment =>
        roads
          .flatMap(_.lanes)
          .map { lane =>
            lane -> lane
              .intersectionPoints(segment)
              .map(lane.normalizedDistanceAlongLane)
          }
          .filter {
            case (_, distances) => distances.nonEmpty
          }
      }
      .foldLeft(
        (
          Map.empty[Lane, Double],
          Map.empty[Lane, Double]
        )
      ) {
        case ((minDistances, maxDistances), (lane, distances)) =>
          val newMinDistance = if (minDistances.contains(lane)) {
            (minDistances(lane) :: distances).min
          } else {
            distances.min
          }
          val newMaxDistance = if (maxDistances.contains(lane)) {
            (maxDistances(lane) :: distances).max
          } else {
            distances.max
          }

          val updatedMinDistances: Map[Lane, Double] =
            minDistances.updated(lane, newMinDistance)
          val updatedMaxDistances: Map[Lane, Double] =
            maxDistances.updated(lane, newMaxDistance)

          (updatedMinDistances, updatedMaxDistances)
      }
  }

  override type Self = RoadIntersection
  def entitySettings: EntitySettings[RoadIntersection] = RoadIntersection

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
      RoadIntersection.shapeFactory.makeShapeFromGeometry(intersectionGeometry)
    val (minDistanceFromLanes, maxDistanceFromLanes) =
      extractIntersectionPointDistances(geometrySegments, roads)
    val intersectingLanes = minDistanceFromLanes.keySet

    intersectingLanes.foldLeft(IntersectionAreaSegments()) {
      case (IntersectionAreaSegments(geometry,
                                     inletPoints,
                                     outletPoints,
                                     inletHeadings,
                                     outletHeadings),
            lane) =>
        //TODO czy na pewno powinno być zależne od długości pasa?
        val expansionOffset = RoadIntersection.expansionDistance / lane.length
        val (entryFraction, newEntryPoints, newEntryHeadings) =
          if (intersectionShape.relate(lane.entryPoint).intersects()) {
            (0d, inletPoints, inletHeadings)
          } else {
            val fraction = (minDistanceFromLanes(lane) - expansionOffset).meters max 0d
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
            val fraction = (maxDistanceFromLanes(lane) - expansionOffset).meters min 1d
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
        val filledGeometryFragment = geometryFragment.union {
          geometryFragment.symDifference(geometryFragment)
        }
        val newGeometry = geometry match {
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

object RoadIntersection extends EntitySettings[RoadIntersection] {
  //Offset for additional space at intersection, e.q crosswalks
  val expansionDistance: Dimension = 4.0
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
