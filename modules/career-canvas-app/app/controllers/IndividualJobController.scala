package controllers

import authentication.{AuthenticatedUserAction, AuthenticatedUserMessagesAction}
import careercanvas.io.converter.Converters
import careercanvas.io.model.job._
import model.Global
import service.JobApplicationsService
import play.api.mvc._
import play.api.libs.json.{JsSuccess, JsValue, Json, Reads}

import javax.inject.Inject

class IndividualJobController @Inject()(
  cc: MessagesControllerComponents,
  jobApplicationsService: JobApplicationsService,
  authenticatedUserAction: AuthenticatedUserAction,
  authenticatedUserMessagesAction: AuthenticatedUserMessagesAction
) extends MessagesAbstractController(cc) with Converters {

  private val logger = play.api.Logger(this.getClass)

  implicit val optionStringReads: Reads[Option[String]] = Reads.optionWithNull[String]

  def showJobView(jobInfo: JobInfo): Action[AnyContent] = authenticatedUserMessagesAction { implicit request: MessagesRequest[AnyContent] =>
    val jobDescriptions = jobApplicationsService.resolveJobDescriptions(jobInfo.jobId)
    Ok(views.html.authenticated.user.job.IndividualJobOverview(jobInfo, jobDescriptions))
  }

  def deleteJob(jobId: Long): Action[AnyContent] = authenticatedUserAction { implicit request =>
    val userId = retrieveUserId(request)
    jobApplicationsService.deleteJob(userId, jobId)
    Redirect(routes.JobFeedController.showJobFeedHome())
  }

  def updateStatus(): Action[JsValue] = authenticatedUserMessagesAction(parse.json) { implicit request =>
    val userId = retrieveUserId(request)
    val jobIdValidation = (request.body \ "jobId").validate[String]
    val statusValidation = (request.body \ "status").validate[JobStatus]

    (jobIdValidation, statusValidation) match {
      case (JsSuccess(jobId, _), JsSuccess(status, _)) =>
        jobApplicationsService.updateStatus(userId, jobId, status)
        Ok(Json.obj("success" -> "Updated status"))
      case _ =>
        BadRequest(Json.obj("error" -> "Invalid JSON format"))
    }
  }

  def updateJobType(): Action[JsValue] = authenticatedUserMessagesAction(parse.json) { implicit request =>
    val userId = retrieveUserId(request)
    val jobIdValidation = (request.body \ "jobId").validate[String]
    val jobTypeValidation = (request.body \ "jobType").validate[JobType]

    (jobIdValidation, jobTypeValidation) match {
      case (JsSuccess(jobId, _), JsSuccess(jobType, _)) =>
        jobApplicationsService.updateJobType(userId, jobId, jobType)
        Ok(Json.obj("success" -> "Updated job type"))
      case _ =>
        BadRequest(Json.obj("error" -> "Invalid JSON format"))
    }
  }

  def updateNotes(jobId: Long): Action[JsValue] = authenticatedUserMessagesAction(parse.json) { implicit request =>
    val userId = retrieveUserId(request)
    val jobInfo = jobApplicationsService.getJobById(userId, jobId)
    val updatedNotesValidation = (request.body \ "updateText").validate[String]

    updatedNotesValidation match {
      case JsSuccess(updatedNotes, _) =>
        jobApplicationsService.updateNotes(jobInfo.jobId, updatedNotes)
        Redirect(routes.IndividualJobController.showJobView(jobInfo))
          .flashing("success" -> "Updated notes section")
      case _ =>
        Redirect(routes.IndividualJobController.showJobView(jobInfo))
          .flashing("error" -> "Invalid JSON format")
    }
  }

  def updateAbout(jobId: Long): Action[JsValue] = authenticatedUserMessagesAction(parse.json) { implicit request =>
    val userId = retrieveUserId(request)
    val jobInfo = jobApplicationsService.getJobById(userId, jobId)
    val updatedAboutValidation = (request.body \ "updateText").validate[String]

    updatedAboutValidation match {
      case JsSuccess(updatedAbout, _) =>
        jobApplicationsService.updateAbout(jobInfo.jobId, updatedAbout)
        Redirect(routes.IndividualJobController.showJobView(jobInfo))
          .flashing("success" -> "Updated about section")
      case _ =>
        Redirect(routes.IndividualJobController.showJobView(jobInfo))
          .flashing("error" -> "Invalid JSON format")
     }
  }

  def updateRequirements(jobId: Long): Action[JsValue] = authenticatedUserMessagesAction(parse.json) { implicit request =>
    val userId = retrieveUserId(request)
    val jobInfo = jobApplicationsService.getJobById(userId, jobId)
    val updatedRequirementsValidation = (request.body \ "updateText").validate[String]

    updatedRequirementsValidation match {
      case JsSuccess(updatedRequirements, _) =>
        jobApplicationsService.updateRequirements(jobInfo.jobId, updatedRequirements)
        Redirect(routes.IndividualJobController.showJobView(jobInfo))
          .flashing("success" -> "Updated requirements section")
      case _ =>
        Redirect(routes.IndividualJobController.showJobView(jobInfo))
          .flashing("error" -> "Invalid JSON format")
    }
  }

  def updateTechStack(jobId: Long): Action[JsValue] = authenticatedUserMessagesAction(parse.json) { implicit request =>
    val userId = retrieveUserId(request)
    val jobInfo = jobApplicationsService.getJobById(userId, jobId)
    val updatedTechStackValidation = (request.body \ "updateText").validate[String]

    updatedTechStackValidation match {
      case JsSuccess(updatedTechStack, _) =>
        jobApplicationsService.updateTechStack(jobInfo.jobId, updatedTechStack)
        Redirect(routes.IndividualJobController.showJobView(jobInfo))
          .flashing("success" -> "Updated tech stack section")
      case _ =>
        Redirect(routes.IndividualJobController.showJobView(jobInfo))
          .flashing("error" -> "Invalid JSON format")
    }
  }

  private def retrieveUserId(request: RequestHeader): String = request.session.data(Global.SESSION_USER_ID)

}
