import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.Docker

import scala.tools.util.PathResolver.Environment

enablePlugins(JavaAppPackaging)

val http4sVersion = "0.23.30"
val circeVersion = "0.14.13"
val doobieVersion = "1.0.0-RC9"
val scalaTestVersion = "3.2.19"
val kittensVersion = "3.5.0"
val logbackVersion = "1.5.18"

organization := "net.raystarkmc"
version := "0.1.0-SNAPSHOT"
scalaVersion := "3.7.0"
scalacOptions ++= Seq(
  "-Wnonunit-statement",
  "-Yexplicit-nulls",
  "-Xkind-projector:underscores",
  "-source:future",
  "-deprecation",
)

Test / scalacOptions -= "-Wnonunit-statement"

scalafmtConfig := file("./../.scalafmt.conf")
scalafmtFilter := sys.env.get("GITHUB_BASE_REF").fold("")("diff-ref=" ++ _)

lazy val root = (project in file("."))
  .settings(
    name := "api",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-ember-client" % http4sVersion,
      "org.http4s" %% "http4s-ember-server" % http4sVersion,
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-literal" % circeVersion,
      "org.typelevel" %% "kittens" % kittensVersion,
      "ch.qos.logback" % "logback-classic" % logbackVersion,
      "org.tpolecat" %% "doobie-core" % doobieVersion,
      "org.tpolecat" %% "doobie-postgres" % doobieVersion,
      "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
      "org.scalatest" %% "scalatest-freespec" % scalaTestVersion % "test",
    ),
    dockerBaseImage := "amazoncorretto:21",
    dockerExposedPorts := Seq(8080),
    Compile / run / fork := true,
    Docker / packageName := "crafting-simulator",
    Docker / daemonUserUid := None,
    Docker / daemonUser := "daemon",
  )
