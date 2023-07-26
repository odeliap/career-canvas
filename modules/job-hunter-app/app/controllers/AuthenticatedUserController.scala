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
  def home() = authenticatedUserAction { implicit request: Request[AnyContent] =>
    Ok(views.html.home())
  }

  def feed() = authenticatedUserAction { implicit request: Request[AnyContent] =>
    Ok(views.html.feed())
  }

  def network() = authenticatedUserAction { implicit request: Request[AnyContent] =>
    Ok(views.html.network())
  }

  def jobStats() = authenticatedUserAction { implicit request: Request[AnyContent] =>
    Ok(views.html.jobStats())
  }

  def logout = authenticatedUserAction { implicit request: Request[AnyContent] =>
    // docs: “withNewSession ‘discards the whole (old) session’”
    Redirect(routes.UserController.showLoginForm)
      .flashing("info" -> "You are logged out.")
      .withNewSession
  }

}
