import sbt._
import Libraries._

object Dependencies {

  lazy val server: Seq[ModuleID] = Libraries.clean(
    akkaActors,
    akkaCluster,
    akkaStream,
    akkaHttp
  )

  lazy val model: Seq[ModuleID] =
    Libraries.clean(geospatial, marshalling, math, akkaActors)

  lazy val simulation: Seq[ModuleID] = Libraries.clean()
}
