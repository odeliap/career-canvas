package controllers

import authentication.AuthenticatedUserAction
import model.Global
import play.api.libs.json.Json

import javax.inject._
import play.api.mvc._
import play.twirl.api.Html
import service.JobStatisticsService

@Singleton
class AuthenticatedUserController @Inject()(
  cc: ControllerComponents,
  authenticatedUserAction: AuthenticatedUserAction,
  jobStatisticsService: JobStatisticsService
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

  def showJobStats(): Action[AnyContent] = authenticatedUserAction { implicit request: Request[AnyContent] =>
    val userId = request.session.data(Global.SESSION_USER_ID)
    val statusPercentages = jobStatisticsService.getStatusBreakdown(userId)
    val statusPercentagesHtml = new Html(Json.toJson(statusPercentages).toString())
    Ok(views.html.authenticated.user.jobStats(statusPercentagesHtml))
  }

  def logout: Action[AnyContent] = authenticatedUserAction { implicit request: Request[AnyContent] =>
    Redirect(routes.UserController.showLoginForm())
      .flashing("info" -> "You are logged out.")
      .withNewSession
  }

}
