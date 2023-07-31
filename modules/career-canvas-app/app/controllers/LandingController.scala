package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class LandingController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def showWelcomePage(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.landing.welcomePage())
  }

  def showAboutPage(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.landing.about())
  }

}