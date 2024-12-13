ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.15"

lazy val root = (project in file(".")).settings(name := "data-processing-optimization")
ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % "early-semver"
ThisBuild / evictionErrorLevel                                   := Level.Warn

libraryDependencies ++=
  Seq(
    "org.apache.spark"             %% "spark-core"           % "3.5.1",
    "org.apache.spark"             %% "spark-sql"            % "3.5.1",
    "org.apache.spark"             %% "spark-yarn"           % "3.5.1",
    "org.apache.spark"             %% "spark-sql-kafka-0-10" % "3.5.1",
    "org.apache.spark"             %% "spark-protobuf"       % "3.5.1",
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.15.2",
    "com.google.cloud.bigdataoss"   % "gcs-connector"        % "hadoop3-2.2.5",
    "com.thesamet.scalapb"         %% "scalapb-runtime"      % "0.11.6",
    "com.typesafe.akka"            %% "akka-actor-typed"     % "2.7.0",
    "com.typesafe.akka"            %% "akka-stream"          % "2.7.0",
    "ch.qos.logback"                % "logback-classic"      % "1.4.11",
    "com.typesafe.akka"            %% "akka-stream-kafka"    % "4.0.2"
  )