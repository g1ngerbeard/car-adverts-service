name := "car-adverts-service"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies ++= {
  val akkaHttpVersion = "10.0.3"
  val slf4jVersion = "1.6.4"
  val scalaTestVersion = "3.0.1"
  val typeSafeConfig = "1.3.1"

  Seq(
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
    "org.slf4j" % "slf4j-nop" % slf4jVersion,
    "com.typesafe" % "config" % typeSafeConfig
  )
}