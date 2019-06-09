import sbt._
import Libraries._

object Dependencies {
//  lazy val core: Seq[ModuleID] = Libraries.clean(
//    libGdxCore,
//  )
//
//  lazy val desktop: Seq[ModuleID] = Libraries.clean(
//    libGdxDesktop
//  )

  lazy val server: Seq[ModuleID] = Libraries.clean(
    akkaActors,
    akkaCluster,
    akkaStream,
    akkaHttp
  )

  lazy val model: Seq[ModuleID] = Libraries.clean(marshalling, akkaActors)

}
