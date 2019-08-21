package pl.edu.agh.wmazur.avs.model.entity.intersection
import akka.actor.typed.ActorRef
import org.locationtech.jts.geom.{Geometry, LineString, Point => JtsPoint}
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

    def lineStringLength(geometry: Geometry): Dimension = {
      geometry match {
        case line: LineString => line.getLength.geoDegrees
        case other            => sys.error(s"This should not happend, received $other")
      }
    }

    if (arrivalLane == departureLane) {
      (
        entryPoints(arrivalLane) distance exitPoints(departureLane)
      ).geoDegrees
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

      if (!exitPoints.contains(departureLane) || !entryPoints.contains(
            arrivalLane)) {
        println("!!!")
      }

      val arrivalLaneIntersectionSegment =
        LineFactory(entryPoints(arrivalLane), arrivalLaneEnd)
      val departureLineIntersectionSegment =
        LineFactory(departureLaneStart, exitPoints(departureLane))

      val segmentsIntersects = arrivalLaneIntersectionSegment
        .relate(departureLineIntersectionSegment)
        .isIntersects

      val segments = if (segmentsIntersects) {
        val intersectionPoint =
          arrivalLaneIntersectionSegment.intersection(
            departureLineIntersectionSegment) match {
            case point: JtsPoint => Point2(point.getX, point.getY)
            case point: Point    => point
            case other           => sys.error(s"This should not happend, received $other")
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
        .sum
    }
  }

  override lazy val area: Shape =
    AutonomousRoadIntersection.shapeFactory.makeShapeFromGeometry {
      iaSegments.geometry.get
    }

  override lazy val position: Point = centroid

  private def findIntersectionGeometry(roads: Iterable[Road]): Geometry = {
    val geometries = roads.map(_.geometry).toSet
    val intersectionAreas = geometries
      .subsets(2)
      .map(_.toList)
      .collect {
        case left :: right :: Nil if left.intersects(right) =>
          left.intersection(right)
      }

    intersectionAreas
      .reduce(_.union(_))
      .convexHull()
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
        val expansionOffset = AutonomousRoadIntersection.expansionDistance.asMeters / lane.length.asMeters
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
  //Offset for additional space at intersection, e.q
  val expansionDistance: Dimension = 2.0.meters
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
