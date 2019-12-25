name := """scala-play-wet-paw"""
organization := "mukov"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)
resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  guice,
  "com.typesafe.play" %% "play-slick" % "4.0.2",
  "com.typesafe.play" %% "play-slick-evolutions" % "4.0.2",
  "mysql" % "mysql-connector-java" % "8.0.15",
  "org.mindrot" % "jbcrypt" % "0.4",
  "com.jason-goodwin" %% "authentikat-jwt" % "0.4.5"
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "mukov.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "mukov.binders._"
