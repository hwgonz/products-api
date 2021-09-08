import sbt._

object Dependencies {

  lazy val http4sVersion          = "0.21.22"
  lazy val sttpVersion            = "3.3.6"
  lazy val tapirVersion           = "0.17.19"
  lazy val circeVersion           = "0.13.0"
  lazy val zioVersion             = "1.0.6"
  lazy val zioInteropCatsVersion  = "2.4.0.0"
  lazy val zioLoggingVersion      = "0.5.10"
  lazy val zioLoggingSlf4jVersion = "0.5.10"
  lazy val doobieVersion          = "0.12.1"
  lazy val pureConfigVersion      = "0.15.0"
  lazy val slf4jVersion           = "1.7.30"
  lazy val catsVersion            = "2.6.1"

  lazy val httpServer = Seq(
    //"org.http4s" %% "http4s-blaze-client" % http4sVersion,
    "org.http4s" %% "http4s-blaze-server" % http4sVersion,
    "org.http4s" %% "http4s-circe"        % http4sVersion,
    "org.http4s" %% "http4s-dsl"          % http4sVersion
  )

  lazy val httpClient = Seq(
    "com.softwaremill.sttp.client3" %% "async-http-client-backend-zio" % sttpVersion,
    "com.softwaremill.sttp.tapir"   %% "tapir-zio"                     % tapirVersion,
    "com.softwaremill.sttp.tapir"   %% "tapir-zio-http4s-server"       % tapirVersion
  )

  lazy val docs = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"       % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-http4s"  % tapirVersion
  )

  lazy val json = Seq(
    //"com.softwaremill.sttp.client3" %% "circe" % sttpVersion,
    //"com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirVersion,
    //"io.circe" %% "circe-core" % circeVersion,
    "io.circe" %% "circe-generic"        % circeVersion,
    "io.circe" %% "circe-generic-extras" % circeVersion
    //"io.circe" %% "circe-literal" % circeVersion,
    //"io.circe" %% "circe-parser" % circeVersion
  )

  lazy val zioDeps = Seq(
    "dev.zio" %% "zio"              % zioVersion,
    "dev.zio" %% "zio-interop-cats" % zioInteropCatsVersion,
    //"dev.zio" %% "zio-logging" % zioLoggingVersion,
    //"dev.zio" %% "zio-logging-slf4j" % zioLoggingSlf4jVersion,
    //"dev.zio" %% "zio-macros" % zioVersion,
    "dev.zio" %% "zio-test"     % zioVersion % Test,
    "dev.zio" %% "zio-test-sbt" % zioVersion % Test
  )

  lazy val database = Seq(
    "org.tpolecat" %% "doobie-core" % doobieVersion,
    //"org.tpolecat" %% "doobie-hikari" % doobieVersion,
    "org.tpolecat" %% "doobie-h2" % doobieVersion
    //"org.tpolecat" %% "doobie-scalatest" % doobieVersion
  )

  lazy val configuration = Seq(
    "com.github.pureconfig" %% "pureconfig" % pureConfigVersion
    //"com.github.pureconfig" %% "pureconfig-sttp" % pureConfigVersion
  )

  lazy val sl4jLogging = Seq(
    "org.slf4j" % "slf4j-log4j12" % slf4jVersion
  )

  lazy val cats = Seq(
    "org.typelevel" %% "cats-core" % catsVersion
  )

}
