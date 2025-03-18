ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.5"

lazy val root = (project in file("."))
  .settings(
    name := "scala_ftp_bck"
  )

libraryDependencies ++= Seq(
  "com.h2database" % "h2" % "2.3.232"
)