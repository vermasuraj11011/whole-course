ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.15"

lazy val root = (project in file(".")).settings(name := "sensor-data")

resolvers += "Akka Repository" at "https://repo.akka.io/maven"

libraryDependencies ++=
  Seq(
    "org.apache.spark"           %% "spark-core"           % "3.5.1",
    "org.apache.spark"           %% "spark-sql"            % "3.5.1",
    "org.apache.spark"           %% "spark-yarn"           % "3.5.1",
    "org.apache.spark"           %% "spark-sql-kafka-0-10" % "3.5.1",
    "org.apache.spark"           %% "spark-protobuf"       % "3.5.1",
    "com.google.cloud.bigdataoss" % "gcs-connector"        % "hadoop3-2.2.25",
    "com.thesamet.scalapb"       %% "scalapb-runtime"      % "0.11.17",
    "com.typesafe.akka"          %% "akka-actor-typed"     % "2.9.6",
    "com.typesafe.akka"          %% "akka-stream"          % "2.9.6",
    "ch.qos.logback"              % "logback-classic"      % "1.5.12",
    "com.typesafe.akka"          %% "akka-stream-kafka"    % "6.0.0",
    "com.typesafe.akka"          %% "akka-http"            % "10.6.0",
    "ch.megard"                  %% "akka-http-cors"       % "1.2.0"
  )
