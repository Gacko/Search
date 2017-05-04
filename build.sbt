name := "pr0lastic"

organization := "com.github.gacko"

version := "1.0"

lazy val root = project in file(".") enablePlugins PlayScala

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  ws,
  guice,
  "org.elasticsearch.client" % "transport" % "5.4.0",
  "org.apache.logging.log4j" % "log4j-api" % "2.8.2",
  "org.apache.logging.log4j" % "log4j-to-slf4j" % "2.8.2",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0-M3" % Test
)
