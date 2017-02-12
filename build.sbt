import com.typesafe.sbt.SbtNativePackager.autoImport._
import com.typesafe.sbt.packager.archetypes.JavaAppPackaging
import com.typesafe.sbt.packager.docker.DockerPlugin
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport._
import spray.revolver.RevolverPlugin.autoImport.Revolver

name := "car-adverts-service"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies ++= {
  val akkaHttpVersion = "10.0.3"
  val slf4jVersion = "1.6.4"
  val scalaTestVersion = "3.0.1"
  val typeSafeConfigVersion = "1.3.1"
  val awsScalaVersion = "0.5.+"
  val mongoDriverVersion = "1.2.1"
  val embedMongo = "1.21"

  Seq(
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % "test",
    "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
    "org.slf4j" % "slf4j-nop" % slf4jVersion,
    "com.typesafe" % "config" % typeSafeConfigVersion,
    "com.github.seratch" %% "awscala" % awsScalaVersion,
    "org.mongodb.scala" %% "mongo-scala-driver" % mongoDriverVersion,
    "de.flapdoodle.embed" % "de.flapdoodle.embed.mongo" % embedMongo % "test"
  )
}

Revolver.settings
enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)

dockerEntrypoint := Seq("bin/%s" format executableScriptName.value)
