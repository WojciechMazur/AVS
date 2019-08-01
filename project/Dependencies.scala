import sbt._
import Libraries._

object Dependencies {

  lazy val backend: Seq[ModuleID] =
    Libraries.clean(akkaActors,
                    akkaCluster,
                    akkaStream,
                    akkaHttp,
                    geospatial,
                    marshalling,
                    math)
}
