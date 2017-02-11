name := "pr0lastic"

organization := "com.github.gacko"

version := "1.0"

lazy val root = project in file(".") enablePlugins PlayScala

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  ws,
  "org.elasticsearch" % "elasticsearch" % "2.4.4",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)
