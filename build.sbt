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
    libraries.slick.postgresDriver,
    libraries.standard.betterFiles
  )
)

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(commonSettings)
  .settings(
    name := "career-canvas",
    description := "Career Canvas service",
    organization := "odeliaputterman.com",
    version := "1.0-SNAPSHOT"
  )
  .aggregate(careerCanvasApp, careerCanvasDatasource, careerCanvasModel, careerCanvasProcessor)

lazy val careerCanvasApp = (project in file("modules/career-canvas-app"))
  .enablePlugins(PlayScala)
  .settings(commonSettings)
  .settings(
    name := "career-canvas-app",
    description := "Career Canvas play app",
    libraryDependencies ++= Seq(
      specs2 % Test,
      libraries.test.scalaTestPlus % Test
    )
  )
  .dependsOn(
    careerCanvasDatasource, careerCanvasModel, careerCanvasProcessor
  )

lazy val careerCanvasDatasource = (project in file("modules/career-canvas-datasource"))
  .settings(commonSettings)
  .settings(
    name := "career-canvas-datasource",
    description := "Career Canvas data source",
    libraryDependencies ++= Seq(
    )
  )
  .dependsOn(
    careerCanvasModel
  )

lazy val careerCanvasModel = (project in file("modules/career-canvas-model"))
  .settings(commonSettings)
  .settings(
    name := "career-canvas-model",
    description := "Career Canvas model",
    libraryDependencies ++= Seq(
    )
  )

lazy val careerCanvasProcessor = (project in file("modules/career-canvas-processor"))
  .settings(commonSettings)
  .settings(
    name := "career-canvas-processor",
    description := "Career Canvas processor",
    libraryDependencies ++= Seq(
      libraries.scraper.jsoup,
      libraries.openai.client,
      libraries.openai.guice,
      libraries.aws.s3
    )
  ).dependsOn(
    careerCanvasModel
  )