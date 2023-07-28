import sbt._

object Dependencies {

  object versions {
    object test{
      val scalaTestPlusVersion = "5.1.0"
    }
  }

  object libraries {
    object test {
      import versions.test._
      val scalaTestPlus = "org.scalatestplus.play" %% "scalatestplus-play" % scalaTestPlusVersion
    }
  }

}
