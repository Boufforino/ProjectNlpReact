name := "tpScalaNLP"

version := "0.1"
scalaVersion := "2.12.8"
libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.1"
// Akka dependencies
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.5.21"
)
libraryDependencies += "com.typesafe.akka" %% "akka-remote" % "2.5.21"
libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % "0.18.21",
  "org.http4s" %% "http4s-blaze-server" % "0.18.21",
  "org.http4s" %% "http4s-blaze-client" % "0.18.21",
  "org.http4s" %% "http4s-circe" % "0.18.21",
  "ch.qos.logback"  %  "logback-classic" % "1.1.3"
)
scalacOptions ++= Seq("-Ypartial-unification")
val circeVersion = "0.10.0"
libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

libraryDependencies += "com.github.pathikrit" %% "better-files" % "3.7.1"