package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class PolicyController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def showTermsOfUse(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.help.center.HelpCenterTermsOfUseView())
  }

  def showPrivacyPolicy(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.help.center.HelpCenterPrivacyPolicyView())
  }

  def showCookiesPolicy(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.help.center.HelpCenterCookiesPolicyView())
  }
}