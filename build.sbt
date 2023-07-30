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
    guice,
    libraries.play.slick,
    libraries.play.evolutions,
    libraries.play.json,
    libraries.slick.core,
    libraries.slick.postgres,
    libraries.slick.postgresJson,
    libraries.slick.postgresDriver
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
  .aggregate(jobHunterApp, jobHunterDatasource, jobHunterModel)

lazy val jobHunterApp = (project in file("modules/job-hunter-app"))
  .enablePlugins(PlayScala)
  .settings(commonSettings)
  .settings(
    name := "job-hunter-app",
    description := "Job Hunter play app",
    libraryDependencies ++= Seq(
      specs2 % Test,
      libraries.test.scalaTestPlus % Test,
    )
  )
  .dependsOn(
    jobHunterDatasource, jobHunterModel
  )

lazy val jobHunterDatasource = (project in file("modules/job-hunter-datasource"))
  .settings(commonSettings)
  .settings(
    name := "job-hunter-datasource",
    description := "Job Hunter data source",
    libraryDependencies ++= Seq(
    )
  )
  .dependsOn(
    jobHunterModel
  )

lazy val jobHunterModel = (project in file("modules/job-hunter-model"))
  .settings(commonSettings)
  .settings(
    name := "job-hunter-model",
    description := "Job Hunter model",
    libraryDependencies ++= Seq(
    )
  )