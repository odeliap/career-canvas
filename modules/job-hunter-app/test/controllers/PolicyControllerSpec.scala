package controllers

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Injecting}

class PolicyControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "PolicyController GET" should {
    "render the terms of use page from a new instance of controller" in {
      val controller = new PolicyController(stubControllerComponents())
      val termsOfUse = controller.showTermsOfUse().apply(FakeRequest(GET, "/helpCenter/termsOfUse"))

      status(termsOfUse) mustBe OK
      contentType(termsOfUse) mustBe Some("text/html")
    }
    "render the privacy policy page from a new instance of controller" in {
      val controller = new PolicyController(stubControllerComponents())
      val privacyPolicy = controller.showPrivacyPolicy().apply(FakeRequest(GET, "/helpCenter/privacyPolicy"))

      status(privacyPolicy) mustBe OK
      contentType(privacyPolicy) mustBe Some("text/html")
    }
    "render the cookies policy page from a new instance of controller" in {
      val controller = new PolicyController(stubControllerComponents())
      val cookiesPolicy = controller.showCookiesPolicy().apply(FakeRequest(GET, "/helpCenter/cookiesPolicy"))

      status(cookiesPolicy) mustBe OK
      contentType(cookiesPolicy) mustBe Some("text/html")
    }
    "render the terms of use page from the application" in {
      val controller = inject[PolicyController]
      val termsOfUse = controller.showTermsOfUse().apply(FakeRequest(GET, "/helpCenter/termsOfUse"))

      status(termsOfUse) mustBe OK
      contentType(termsOfUse) mustBe Some("text/html")
    }
    "render the privacy policy page from the application" in {
      val controller = inject[PolicyController]
      val privacyPolicy = controller.showPrivacyPolicy().apply(FakeRequest(GET, "/helpCenter/privacyPolicy"))

      status(privacyPolicy) mustBe OK
      contentType(privacyPolicy) mustBe Some("text/html")
    }
    "render the cookies policy page from the application" in {
      val controller = inject[PolicyController]
      val cookiesPolicy = controller.showCookiesPolicy().apply(FakeRequest(GET, "/helpCenter/cookiesPolicy"))

      status(cookiesPolicy) mustBe OK
      contentType(cookiesPolicy) mustBe Some("text/html")
    }
    "render the terms of use page from the router" in {
      val request = FakeRequest(GET, "/helpCenter/termsOfUse")
      val termsOfUse = route(app, request).get

      status(termsOfUse) mustBe OK
      contentType(termsOfUse) mustBe Some("text/html")
    }
    "render the privacy policy page from the router" in {
      val request = FakeRequest(GET, "/helpCenter/privacyPolicy")
      val privacyPolicy = route(app, request).get

      status(privacyPolicy) mustBe OK
      contentType(privacyPolicy) mustBe Some("text/html")
    }
    "render the cookies policy page from the router" in {
      val request = FakeRequest(GET, "/helpCenter/cookiesPolicy")
      val cookiesPolicy = route(app, request).get

      status(cookiesPolicy) mustBe OK
      contentType(cookiesPolicy) mustBe Some("text/html")
    }
  }

}
