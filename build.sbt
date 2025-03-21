ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.5"

lazy val root = (project in file("."))
  .settings(
    name := "scala_ftp_bck"
  )

libraryDependencies ++= Seq(
  "com.lihaoyi" %% "fansi" % "0.5.0",
  "org.xerial" % "sqlite-jdbc" % "3.49.1.0",
  "commons-net" % "commons-net" % "3.11.1"
)