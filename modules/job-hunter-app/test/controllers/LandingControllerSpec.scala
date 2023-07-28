package controllers

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Injecting}

class LandingControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "LandingController GET" should {
    "render the landing page from a new instance of controller" in {
      val controller = new LandingController(stubControllerComponents())
      val welcome = controller.showWelcomePage().apply(FakeRequest(GET, "/"))

      status(welcome) mustBe OK
      contentType(welcome) mustBe Some("text/html")
    }
    "render the about page from a new instance of controller" in {
      val controller = new LandingController(stubControllerComponents())
      val about = controller.showAboutPage().apply(FakeRequest(GET, "/about"))

      status(about) mustBe OK
      contentType(about) mustBe Some("text/html")
    }
    "render the landing page from the application" in {
      val controller = inject[LandingController]
      val landing = controller.showAboutPage().apply(FakeRequest(GET, "/"))

      status(landing) mustBe OK
      contentType(landing) mustBe Some("text/html")
    }
    "render the about page from the application" in {
      val controller = inject[LandingController]
      val about = controller.showAboutPage().apply(FakeRequest(GET, "/about"))

      status(about) mustBe OK
      contentType(about) mustBe Some("text/html")
    }
    "render the landing page from the router" in {
      val request = FakeRequest(GET, "/")
      val welcome = route(app, request).get

      status(welcome) mustBe OK
      contentType(welcome) mustBe Some("text/html")
    }
    "render the about page from the router" in {
      val request = FakeRequest(GET, "/about")
      val about = route(app, request).get

      status(about) mustBe OK
      contentType(about) mustBe Some("text/html")
    }
  }

}
