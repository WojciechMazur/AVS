import Settings._

name := "AVS"

version := "0.1"

scalaVersion := Libraries.Versions.scala

resolvers += "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"

lazy val root = (project in file("."))
  .aggregate(backend, frontend)

lazy val frontend: Project = module("avs-gui")
  .settings(withCommons():_*)


lazy val backend: Project = module("avs-backend")
  .settings(withCommons():_*)
  .settings(withCommons(
    scalaPbSettings,
    PB.protoSources in Compile := Seq(
      root.base.getAbsoluteFile / "protobuf",
    ),
    libraryDependencies ++= Dependencies.backend
  ):_ *
  )


//lazy val simulation: Project = submodule("avs-backend", "simulation")
//  .settings(withCommons():_*)
//  .dependsOn(model)
//
//lazy val server: Project = submodule("avs-backend", "server")
//  .settings(withCommons(
//    libraryDependencies ++= Dependencies.server,
//  ): _*
//  ).dependsOn(model, simulation)

def submodule(parent: String, moduleName: String): Project = Project(
  id = moduleName,
  base = file (s"$parent/$moduleName")
)
  .settings(
    name := s"$parent/$moduleName"
  )

def module(moduleName: String): Project = Project(
  id = moduleName,
  base = file(moduleName)
).settings(
  name := moduleName
)
