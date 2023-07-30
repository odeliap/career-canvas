package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class AuthenticatedUserController @Inject()(
  cc: ControllerComponents,
  authenticatedUserAction: AuthenticatedUserAction
) extends AbstractController(cc) {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def showHome() = authenticatedUserAction { implicit request: Request[AnyContent] =>
    Ok(views.html.authenticated.user.home())
  }

  def showNetwork() = authenticatedUserAction { implicit request: Request[AnyContent] =>
    Ok(views.html.authenticated.user.network())
  }

  def showJobStats() = authenticatedUserAction { implicit request: Request[AnyContent] =>
    Ok(views.html.authenticated.user.jobStats())
  }

  def logout = authenticatedUserAction { implicit request: Request[AnyContent] =>
    // docs: “withNewSession ‘discards the whole (old) session’”
    Redirect(routes.UserController.showLoginForm)
      .flashing("info" -> "You are logged out.")
      .withNewSession
  }

}
