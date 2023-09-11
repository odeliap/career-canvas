package controllers

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Injecting}

class LandingControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "LandingController GET" should {
    "render the landing page from the router" in {
      val request = FakeRequest(GET, "/")
      val welcome = route(app, request).get

      status(welcome) mustBe OK
      contentType(welcome) mustBe Some("text/html")
    }
  }

}
