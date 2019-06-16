import sbt._
import Libraries._

object Dependencies {

  lazy val server: Seq[ModuleID] = Libraries.clean(
    akkaActors,
    akkaCluster,
    akkaStream,
    akkaHttp
  )

  lazy val model: Seq[ModuleID] = Libraries.clean(marshalling, math, akkaActors)

}
