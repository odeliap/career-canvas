package controllers

import authentication.AuthenticatedUserAction

import javax.inject._
import play.api.mvc._

@Singleton
class AuthenticatedUserController @Inject()(
  cc: ControllerComponents,
  authenticatedUserAction: AuthenticatedUserAction
) extends AbstractController(cc) {

  private val logger = play.api.Logger(this.getClass)

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def showHome(): Action[AnyContent] = authenticatedUserAction { implicit request: Request[AnyContent] =>
    Ok(views.html.authenticated.user.home())
  }

  def logout: Action[AnyContent] = authenticatedUserAction { implicit request: Request[AnyContent] =>
    Redirect(routes.UserController.showLoginForm())
      .flashing("info" -> "You are logged out.")
      .withNewSession
  }

}
