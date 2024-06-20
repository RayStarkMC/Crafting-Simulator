import sbt.Keys.mainClass

ThisBuild / organization := "net.raystarkmc"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.4.2"

lazy val root = (project in file("."))
  .settings(
    name := "crafting-simulator-api",
    Compile / run / mainClass := Some("net.raystarkmc.craftingsimulator.main")
  )
