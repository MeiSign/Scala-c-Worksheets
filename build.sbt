name := """Scala-collaborative-worksheets"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.4"

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-compiler" % "2.11.4",
  "org.scala-lang" % "scala-library"  % "2.11.4"
)

parallelExecution in Test := false