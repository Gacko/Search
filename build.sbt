name := "pr0lastic"

organization := "com.github.gacko"

version := "1.0"

lazy val root = project in file(".") enablePlugins PlayScala

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  ws,
  guice,
  "org.elasticsearch.client" % "transport" % "5.2.1",
  "org.apache.logging.log4j" % "log4j-api" % "2.7",
  "org.apache.logging.log4j" % "log4j-to-slf4j" % "2.7",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)
