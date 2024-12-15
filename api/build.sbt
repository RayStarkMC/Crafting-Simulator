enablePlugins(JavaAppPackaging)

val http4sVersion = "0.23.30"
val circeVersion = "0.14.10"

ThisBuild / organization := "net.raystarkmc"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.4.2"
ThisBuild / scalacOptions ++= Seq(
  "-Wnonunit-statement",
  "-Yexplicit-nulls",
  "-source:future"
)

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
    ),
    Compile / run / fork := true,
    dockerBaseImage := "amazoncorretto:21",
    dockerExposedPorts := Seq(8080)
  )

Docker / packageName := "crafting-simulator"
Docker / daemonUserUid := None
Docker / daemonUser := "daemon"