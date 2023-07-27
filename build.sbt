import Dependencies._

val Scala13 = "2.13.11"

val noPublish = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false
)

val commonSettings = Seq(
  scalaVersion := Scala13,
  fork := true,
  useCoursier := false,
  resolvers ++= Seq(
    Resolver.defaultLocal,
    Resolver.mavenCentral,
    Resolver.mavenLocal
  ),
  libraryDependencies ++= Seq(
    guice
  )
)

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(commonSettings)
  .settings(
    name := "job-hunter",
    description := "Job Hunter service",
    organization := "odeliaputterman.com",
    version := "1.0-SNAPSHOT"
  )
  .aggregate(jobHunterApp)

lazy val jobHunterApp = (project in file("modules/job-hunter-app"))
  .enablePlugins(PlayScala)
  .settings(commonSettings)
  .settings(
    name := "job-hunter-app",
    description := "Job Hunter play app",
    libraryDependencies ++= Seq(
      libraries.test.scalaTestPlus % Test
    )
  )

lazy val jobHunterDatasource = (project in file("modules/job-hunter-datasource"))
  .settings(commonSettings)
  .settings(
    name := "job-hunter-datasource",
    description := "Job Hunter data source",
    libraryDependencies ++= Seq(

    )
  )