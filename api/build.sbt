enablePlugins(JavaAppPackaging)

val http4sVersion = "0.23.30"
val circeVersion = "0.14.10"
val doobieVersion = "1.0.0-RC5"
val scalaTestVersion = "3.2.19"
val ironVersion = "2.6.0"
val kittensVersion = "3.5.0"
val logbackVersion = "1.5.17"

ThisBuild / organization := "net.raystarkmc"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.6.3"
ThisBuild / scalacOptions ++= Seq(
  "-Wnonunit-statement",
  "-Yexplicit-nulls",
  "-Xkind-projector",
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
      "org.typelevel" %% "kittens" % kittensVersion,
      "ch.qos.logback" % "logback-classic" % logbackVersion,
      "org.tpolecat" %% "doobie-core"      % doobieVersion,
      "org.tpolecat" %% "doobie-postgres"  % doobieVersion,
      "io.github.iltotore" %% "iron" % ironVersion,
      "io.github.iltotore" %% "iron-cats" % ironVersion,
      "io.github.iltotore" %% "iron-doobie" % ironVersion,
      "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
      "org.scalatest" %% "scalatest-freespec" % scalaTestVersion % "test"
    ),
    dockerBaseImage := "amazoncorretto:21",
    dockerExposedPorts := Seq(8080),
    Compile / run / fork := true
  )

Docker / packageName := "crafting-simulator"
Docker / daemonUserUid := None
Docker / daemonUser := "daemon"