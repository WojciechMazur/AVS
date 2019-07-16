package pl.edu.agh.wmazur

import org.locationtech.spatial4j.distance.DistanceUtils

package object avs {
  implicit class Dimension(val meters: Double)
      extends AnyVal
      with Ordered[Dimension] {
    def geoDegrees: Double = meters / 1000 * DistanceUtils.KM_TO_DEG

    //scalastyle:off
    def unary_- : Dimension = Dimension(-meters)
    def -(that: Dimension): Dimension = Dimension(this.meters - that.meters)
    def +(that: Dimension): Dimension = Dimension(this.meters + that.meters)
    def *(that: Dimension): Dimension = Dimension(this.meters * that.meters)
    def /(that: Dimension): Dimension = Dimension(this.meters / that.meters)
    def sqrt: Dimension = Math.sqrt(meters)
    //scalastyle:on

    override def compare(that: Dimension): Int = this.meters compare that.meters
  }
}
