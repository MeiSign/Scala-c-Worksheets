name := """Scala-collaborative-worksheets"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "org.scala-lang" % "scala-compiler" % scalaVersion.value
)

parallelExecution in Test := false