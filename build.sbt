import play.Project._

name := """log-auth"""

version := "0.1.0"

libraryDependencies ++= Seq(
  "com.wordnik" %% "swagger-play2" % "1.3.4"
)

scalariformSettings

playScalaSettings
