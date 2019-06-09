import sbt._

object Libraries {
  object Versions {
    lazy val scala = "2.12.8"

    lazy val akka = "2.5.23"
    lazy val akkaHttp = "10.1.8"
    lazy val scalaTest = "3.0.5"
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

  lazy val marshalling: Seq[ModuleID] = Seq(
    "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion
  )

  lazy val common: Seq[ModuleID] = Seq(
    "org.scalactic" %% "scalactic" % Versions.scalaTest,
    "org.scalatest" %% "scalatest" % Versions.scalaTest % "test"
  )

  def clean(deps: Seq[ModuleID]*): Seq[ModuleID] =
    (deps.flatten ++ common).distinct
}
