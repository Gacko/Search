name := "pr0lastic"

organization := "com.github.gacko"

version := "1.0-SNAPSHOT"

lazy val root = project in file(".") enablePlugins PlayScala

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  ws,
  guice,
  "org.elasticsearch.client" % "transport" % "5.6.3",
  "org.apache.logging.log4j" % "log4j-api" % "2.9.1",
  "org.apache.logging.log4j" % "log4j-to-slf4j" % "2.9.1",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
)
