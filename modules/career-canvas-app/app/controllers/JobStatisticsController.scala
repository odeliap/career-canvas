package controllers

import authentication.AuthenticatedUserAction
import model.Global
import play.api.libs.json.Json

import javax.inject._
import play.api.mvc._
import play.twirl.api.Html
import service.JobStatisticsService

@Singleton
class JobStatisticsController @Inject()(
  cc: ControllerComponents,
  authenticatedUserAction: AuthenticatedUserAction,
  jobStatisticsService: JobStatisticsService
) extends AbstractController(cc) {

  private val logger = play.api.Logger(this.getClass)

  def showJobStats(): Action[AnyContent] = authenticatedUserAction { implicit request: Request[AnyContent] =>
    val userId = request.session.data(Global.SESSION_USER_ID)
    val metrics = jobStatisticsService.getMetrics(userId)
    val statusPercentages = jobStatisticsService.cleanStatusBreakdown(metrics.statusPercentages)
    val statusPercentagesHtml = new Html(Json.toJson(statusPercentages).toString())
    Ok(views.html.authenticated.user.statistics.JobStatisticsView(statusPercentagesHtml, metrics.searchDuration, metrics.totalApplications, metrics.totalOffers, metrics.totalRejections))
  }

}
