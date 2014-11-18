import NativePackagerKeys._

packageArchetype.akka_application

name := """Indexer-micro-kernel"""

scalaVersion := "2.11.4"

mainClass in Compile := Some("no.finntech.search.IndexerKernel")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-kernel" % "2.3.7",
  "com.typesafe.akka" %% "akka-actor" % "2.3.7",
  "org.apache.kafka" %% "kafka" % "0.8.2-beta",
  "nl.grons" %% "metrics-scala" % "3.3.0_a2.3"
)
