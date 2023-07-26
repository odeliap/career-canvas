import sbt._

object Dependencies {

  object versions {
    object test{
      val scalaTestPlusVersion = "5.1.0"
    }
  }

  object libraries {
    object test {
      val scalaTestPlus = "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0"
    }
  }

}
