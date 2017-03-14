name := "pr0lastic"

organization := "com.github.gacko"

version := "1.0"

lazy val root = project in file(".") enablePlugins PlayScala

scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  ws,
  guice,
  "org.elasticsearch.client" % "transport" % "5.2.2",
  "org.apache.logging.log4j" % "log4j-api" % "2.7",
  "org.apache.logging.log4j" % "log4j-to-slf4j" % "2.7",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0-M1" % Test
)
