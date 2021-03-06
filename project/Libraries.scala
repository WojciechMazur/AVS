import sbt._

object Libraries {
  object Versions {
    val scala = "2.12.8"

    val akka = "2.5.24"
    val akkaHttp = "10.1.9"
    val chimney = "0.3.2"
    val scalaTest = "3.0.5"
    val scalaGraph = "1.13.0"
    val spatial4j = "0.7"
    val quickLens = "1.4.12"
  }

  import Versions._

  lazy val akkaActors: Seq[ModuleID] = Seq(
    "com.typesafe.akka" %% "akka-actor" % akka,
    "com.typesafe.akka" %% "akka-actor-typed" % akka,
    "com.typesafe.akka" %% "akka-testkit" % akka % Test,
    "com.typesafe.akka" %% "akka-slf4j" % akka,
    "ch.qos.logback" % "logback-classic" % "1.2.3"
  )

  lazy val akkaHttp: Seq[ModuleID] = Seq(
    "com.typesafe.akka" %% "akka-http" % Versions.akkaHttp,
    "com.typesafe.akka" %% "akka-http-testkit" % Versions.akkaHttp % Test
  )

  lazy val akkaStream: Seq[ModuleID] = Seq(
    "com.typesafe.akka" %% "akka-stream" % akka,
    "com.typesafe.akka" %% "akka-stream-typed" % akka,
    "com.typesafe.akka" %% "akka-stream-testkit" % akka % Test
  )

  lazy val akkaCluster: Seq[ModuleID] = Seq(
    "com.typesafe.akka" %% "akka-cluster" % akka,
    "com.typesafe.akka" %% "akka-cluster-sharding" % akka,
    "com.typesafe.akka" %% "akka-distributed-data" % akka,
    "com.typesafe.akka" %% "akka-persistence" % akka
  )

  val math: Seq[ModuleID] = Seq(
//    "org.scalanlp" %% "breeze" % "0.13.2",
    "net.mikera" % "vectorz" % "0.48.0"
  )

  val geospatial: Seq[ModuleID] = Seq(
    "org.locationtech.spatial4j" % "spatial4j" % Versions.spatial4j,
    "org.locationtech.jts" % "jts-core" % "1.16.1",
    "org.scala-graph" %% "graph-core" % Versions.scalaGraph
  )

  lazy val marshalling: Seq[ModuleID] = Seq(
    "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion,
    "io.scalaland" %% "chimney" % Versions.chimney
  )

  lazy val common: Seq[ModuleID] = Seq(
    "com.softwaremill.quicklens" %% "quicklens" % Versions.quickLens,
    "org.scalactic" %% "scalactic" % Versions.scalaTest,
    "org.scalatest" %% "scalatest" % Versions.scalaTest % "test"
  )

  def clean(deps: Seq[ModuleID]*): Seq[ModuleID] =
    (deps.flatten ++ common).distinct
}
