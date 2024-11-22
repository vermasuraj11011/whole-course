ThisBuild / scalaVersion := "2.13.15"
ThisBuild / version      := "1.0"
ThisBuild / name         := "management-project"
ThisBuild / organization := "com.management"

ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % "early-semver"
ThisBuild / evictionErrorLevel                                   := Level.Warn

import scala.collection.Seq

Global / excludeLintKeys += name

lazy val commonSettings =
  Seq(
    Compile / unmanagedSourceDirectories += baseDirectory.value / "app",
    Compile / unmanagedResourceDirectories ++= Seq(baseDirectory.value / "conf", baseDirectory.value / "public"),
    Compile / managedSources ++=
      (Compile / unmanagedSources).value,
    libraryDependencies ++= Seq(guice, "org.playframework" %% "play-guice" % "3.0.0")
  )

lazy val common = (project in file("common")).settings(
  libraryDependencies ++=
    Seq(
      guice,
      "org.playframework" %% "play-guice"                 % "3.0.0",
      "be.objectify"      %% "deadbolt-scala"             % "3.0.0",
      "com.sun.mail"       % "jakarta.mail"               % "2.0.1",
      "com.typesafe.play" %% "play-json"                  % "2.9.2",
      "org.playframework" %% "play-slick"                 % "6.1.0",
      "org.playframework" %% "play-slick-evolutions"      % "6.1.0",
      "mysql"              % "mysql-connector-java"       % "8.0.26",
      "org.playframework" %% "play-ws"                    % "3.0.0",
      "org.apache.kafka"  %% "kafka"                      % "3.2.2",
      "com.typesafe.akka" %% "akka-stream-kafka"          % "4.0.2",
      "com.typesafe.akka" %% "akka-http-spray-json"       % "10.5.0",
      "org.slf4j"          % "slf4j-simple"               % "1.7.36",
      "com.typesafe.akka" %% "akka-actor-typed"           % "2.7.0",
      "com.typesafe.akka" %% "akka-stream"                % "2.7.0",
      "com.typesafe.akka" %% "akka-slf4j"                 % "2.7.0",
      "com.typesafe.akka" %% "akka-serialization-jackson" % "2.7.0"
    )
)

lazy val notification = (project in file("notificationService"))
  .dependsOn(common)
  .settings(
    Compile / mainClass := Some("com.management.notification.NotificationMain"),
    libraryDependencies ++= Seq("org.quartz-scheduler" % "quartz" % "2.3.2")
  )

lazy val apiGateWay = (project in file("apiGatewayService"))
  .dependsOn(common)
  .settings(
    PlayKeys.devSettings := Seq("play.server.http.port" -> "9000"),
    commonSettings,
    libraryDependencies ++=
      Seq("com.typesafe.akka" %% "akka-http" % "10.5.0", "org.playframework" %% "play-ahc-ws" % "3.0.0")
  )
  .enablePlugins(PlayScala)

lazy val userAuth = (project in file("userAuthService"))
  .dependsOn(common)
  .settings(PlayKeys.devSettings := Seq("play.server.http.port" -> "9001"), commonSettings)
  .enablePlugins(PlayScala)

lazy val meeting = (project in file("meetingService"))
  .dependsOn(common)
  .settings(PlayKeys.devSettings := Seq("play.server.http.port" -> "9002"), commonSettings)
  .enablePlugins(PlayScala)

lazy val equipment = (project in file("equipmentService"))
  .dependsOn(common)
  .settings(
    PlayKeys.devSettings := Seq("play.server.http.port" -> "9003"),
    commonSettings,
  )
  .enablePlugins(PlayScala)

lazy val root = (project in file("."))
  .aggregate(common, apiGateWay, userAuth, meeting, equipment, notification)
  .settings(
    libraryDependencies += "org.playframework" %% "play-guice" % "3.0.0",
    Compile / run := {
      val api_val: Unit          = (apiGateWay / Compile / run).toTask("").value
      val user_val: Unit         = (userAuth / Compile / run).toTask("").value
      val meeting_val: Unit      = (meeting / Compile / run).toTask("").value
      val equipment_val: Unit    = (equipment / Compile / run).toTask("").value
      val notification_val: Unit = (notification / Compile / run).toTask("").value
      Def
        .task {
          ()
        }
        .value
    }
  )
  .enablePlugins(PlayScala)
