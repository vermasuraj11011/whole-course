ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.15"
//ThisBuild / scalaVersion := "2.12.18"

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
//    "com.typesafe.akka"            %% "akka-slf4j"           % "2.7.0",
    "ch.qos.logback"     % "logback-classic"   % "1.4.11",
    "com.typesafe.akka" %% "akka-stream-kafka" % "4.0.2"
  )

//"org.apache.spark" %% "spark-core" % "3.5.1",
//"org.apache.spark" %% "spark-sql" % "3.5.1",
//"org.apache.spark" %%"spark-protobuf"% "3.5.1",
// Include SparkSQL Protobuf suppor
// Spark Kafka Integration
//"org.apache.spark" %% "spark-sql-kafka-0-10" % "3.5.1",
//"com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.15.2",
//"com.thesamet.scalapb" %% "scalapb-runtime" % "0.11.6"

//lazy val root = (project in file("."))
//  .settings(
//    name := "kafka-protobuf-scala",
//    resolvers ++= Seq(
//      "Akka Repository" at "https://repo.akka.io/maven/",
//      "Confluent" at "https://packages.confluent.io/maven/"
//    ),
//    libraryDependencies ++= Seq(
//      "com.typesafe.akka" %% "akka-actor" % "2.6.20",
//      "com.typesafe.akka" %% "akka-stream" % "2.6.20",
//      "com.typesafe.akka" %% "akka-stream-kafka" % "2.1.0",
//      "io.confluent" % "kafka-protobuf-serializer" % "7.7.1",
//      "com.thesamet.scalapb" %% "scalapb-runtime" % "0.11.6" // Needed for runtime
//    )
//  )

//  "org.apache.spark"             %% "spark-core"           % "3.4.0",
//  "org.apache.spark"             %% "spark-sql"            % "3.4.0",
//  "org.apache.spark"             %% "spark-yarn"           % "3.4.0",
//  "org.apache.spark"             %% "spark-sql-kafka-0-10" % "3.4.0",
//  "org.apache.spark"             %% "spark-sql-protobuf"   % "3.4.0",
//  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.15.2",
//  "com.google.cloud.bigdataoss"   % "gcs-connector"        % "hadoop3-2.2.5",
//  "com.thesamet.scalapb"         %% "scalapb-runtime"      % "0.11.6"
