name := "home-citytoday"

organization := "eu.citytoday"

version := "1.0"

scalaVersion := "2.11.8"

resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"

libraryDependencies ++= Seq(
  "org.jsoup" % "jsoup" % "1.9.2",
  "org.scalaj" %% "scalaj-http" % "2.3.0",
  "net.sourceforge.htmlcleaner" % "htmlcleaner" % "2.16",
  "net.ruippeixotog" %% "scala-scraper" % "1.0.0",
  "com.lihaoyi" %% "pprint" % "0.4.1",
  "org.scalactic" %% "scalactic" % "3.0.0",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test",
  "com.typesafe.akka" %% "akka-slf4j" % "2.4.9-RC2",
  "com.typesafe.akka" %% "akka-stream" % "2.4.9-RC2",
  "com.typesafe.akka" %% "akka-http-core" % "2.4.9-RC2"
)
    