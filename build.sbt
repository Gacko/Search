name := "pr0lastic"

version := "1.0"

scalaVersion := "2.11.7"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  ws,
  "org.elasticsearch" % "elasticsearch" % "2.4.0",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)
