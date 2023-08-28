import sbt._

object Dependencies {

  object versions {
    object test {
      val scalaTestPlusVersion = "5.1.0"
    }

    object play {
      val slickVersion = "5.0.0"
      val jsonVersion = "2.9.4"
    }

    object slick {
      val slickVersion = "3.3.3"
      val postgresVersion = "0.20.3"
      val postgresDriverVersion = "42.3.4"
    }

    object scraper {
      val jsoupVersion = "1.15.3"
    }

    object openai {
      val clientVersion = "0.1.0"
    }

    object aws {
      val s3Version = "2.19.30"
    }

    object standard {
      val betterFilesVersion = "3.9.1"
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
      val json = "com.typesafe.play" %% "play-json" % jsonVersion
    }

    object slick {
      import versions.slick._
      val core = "com.typesafe.slick" %% "slick" % slickVersion
      val postgres = "com.github.tminglei" %% "slick-pg" % postgresVersion
      val postgresJson = "com.github.tminglei" %% "slick-pg_play-json" % postgresVersion
      val postgresDriver = "org.postgresql" % "postgresql" % postgresDriverVersion
    }

    object scraper {
      import versions.scraper._
      val jsoup = "org.jsoup" % "jsoup" % jsoupVersion
    }

    object openai {
      import versions.openai._
      val client = "io.cequence" %% "openai-scala-client" % clientVersion
      val guice = "io.cequence" %% "openai-scala-guice" % clientVersion
    }

    object aws {
      import versions.aws._
      val s3 = "software.amazon.awssdk" % "s3" % s3Version
    }

    object standard {
      import versions.standard._
      val betterFiles = "com.github.pathikrit" %% "better-files" % betterFilesVersion
    }
  }

}
