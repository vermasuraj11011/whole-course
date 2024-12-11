ThisBuild / version := "1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.15"

lazy val root = (project in file(".")).settings(name := "sensor-data-producer")

libraryDependencies ++= Seq("org.apache.kafka" %% "kafka" % "3.2.2", "org.quartz-scheduler" % "quartz" % "2.3.2")
