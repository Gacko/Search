name := "pr0lastic"

version := "1.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  ws,
  "org.elasticsearch" % "elasticsearch" % "2.3.4",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)
