package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class PolicyController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def showTermsOfUse() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.help.center.termsOfUse())
  }

  def showPrivacyPolicy() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.help.center.privacyPolicy())
  }

  def showCookiesPolicy() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.help.center.cookiesPolicy())
  }
}