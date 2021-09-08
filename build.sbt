/** Project settings */
ThisBuild / scalaVersion := "2.13.3"
ThisBuild / version := "0.1.0-SNAPSHOT"

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-feature",
  "-Xlint"
)
libraryDependencies ++=
  //Dependencies.httpClient ++
  Dependencies.httpServer ++
    //Dependencies.docs ++
    Dependencies.json ++
    Dependencies.zioDeps ++
    Dependencies.configuration ++
    Dependencies.database ++
    Dependencies.sl4jLogging ++
    Dependencies.cats

testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))

scalafmtOnCompile := true
// scalafix; run with `scalafixEnable` followed by `scalafixAll`
ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.4.3"

/** SBT commands */
addCommandAlias("format", ";scalafmtAll;scalafmtSbt")
addCommandAlias("formatCheck", ";scalafmtCheckAll;scalafmtSbtCheck")
