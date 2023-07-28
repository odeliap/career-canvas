import sbt._

object Dependencies {

  object versions {
    object test {
      val scalaTestPlusVersion = "5.1.0"
    }

    object play {
      val slickVersion = "5.0.0"
    }

    object slick {
      val slickVersion = "3.3.3"
      val postgresVersion = "0.20.3"
      val postgresDriverVersion = "42.3.4"
    }
  }

  object libraries {
    object test {
      import versions.test._
      val scalaTestPlus = "org.scalatestplus.play" %% "scalatestplus-play" % scalaTestPlusVersion
    }

    object play {
      import versions.play._
      val slick = "com.typesafe.play" %% "play-slick" % slickVersion
      val evolutions = "com.typesafe.play" %% "play-slick-evolutions" % slickVersion
    }

    object slick {
      import versions.slick._
      val core = "com.typesafe.slick" %% "slick" % slickVersion
      val postgres = "com.github.tminglei" %% "slick-pg" % postgresVersion
      val postgresJson = "com.github.tminglei" %% "slick-pg_play-json" % postgresVersion
      val postgresDriver = "org.postgresql" % "postgresql" % postgresDriverVersion
    }
  }

}
