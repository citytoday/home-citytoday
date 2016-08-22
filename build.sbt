import com.trueaccord.scalapb.{ScalaPbPlugin => PB}

name := "home-citytoday"

organization := "eu.citytoday"

version := "1.0"

scalaVersion := "2.11.8"

resolvers ++= Seq(
  "Artima Maven Repository" at "http://repo.artima.com/releases",
  Resolver.jcenterRepo
)

scalacOptions in ThisBuild ++= Seq(
//  "-Xexperimental",
//  "-Ybackend:GenBCode",
//  "-Ydelambdafy:method",
// "-Ymacro-debug-verbose",
//  "-feature",
  "-target:jvm-1.8"
//  "-deprecation"
)

libraryDependencies ++= Seq(
  "org.jsoup" % "jsoup" % "1.9.2",
  "org.scalaj" %% "scalaj-http" % "2.3.0",
  "net.sourceforge.htmlcleaner" % "htmlcleaner" % "2.16",
  "net.ruippeixotog" %% "scala-scraper" % "1.0.0",
  "com.lihaoyi" %% "pprint" % "0.4.1",
  "org.scalactic" %% "scalactic" % "3.0.0",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test",
  "com.typesafe.akka" %% "akka-slf4j" % "2.4.9",
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "com.typesafe.akka" %% "akka-stream" % "2.4.9",
  "com.typesafe.akka" %% "akka-http-core" % "2.4.9",
  "com.typesafe.akka" %% "akka-stream-kafka" % "0.11-RC1",
  "com.github.os72" % "protoc-jar" % "3.0.0",
  "com.trueaccord.scalapb" %% "scalapb-runtime" % "0.5.38" % PB.protobufConfig,
  "com.trueaccord.scalapb" %% "scalapb-json4s" % "0.1.1",
  "com.iheart" %% "ficus" % "1.2.3"
)

PB.protobufSettings

version in PB.protobufConfig := "3.0.0"

scalaSource in PB.protobufConfig <<= (sourceManaged in Compile)

