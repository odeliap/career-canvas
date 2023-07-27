package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class PolicyController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def termsOfUse() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.termsOfUse())
  }

  def privacyPolicy() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.privacyPolicy())
  }

  def cookiesPolicy() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.cookiesPolicy())
  }
}