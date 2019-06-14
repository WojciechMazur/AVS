import sbt.{Def, _}
import Keys._
import Libraries._
import sbtprotoc.ProtocPlugin.autoImport.PB

object Settings {
  def withCommons(settings: Seq[SettingsDefinition]*): Seq[SettingsDefinition] =
    (common ++ settings.flatten).distinct

  lazy val common: Seq[
    Def.Setting[_ >: String with Task[Seq[String]] with Seq[File] <: Object]] =
    Seq(
      scalaVersion := Versions.scala,
      javacOptions ++= javacCommonOptions,
      scalacOptions in Compile += "-feature",
    )

  lazy val scalaPbSettings: Seq[SettingsDefinition] = Seq(
    PB.targets in Compile := Seq(
      scalapb
        .gen() -> (scalaSource in Compile).value,
    ),
//    PB.deleteTargetDirectory in Compile := false,
    libraryDependencies += "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf"
  )

  private lazy val javacCommonOptions = Seq(
    "-source",
    "11",
    "-target",
    "11",
    "-encoding",
    "UTF-8"
  )

}
