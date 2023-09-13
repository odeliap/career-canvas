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
  authenticatedUserMessagesAction: AuthenticatedUserMessagesAction,
) extends MessagesAbstractController(cc) with Converters {

  private val logger = play.api.Logger(this.getClass)

  implicit val optionStringReads: Reads[Option[String]] = Reads.optionWithNull[String]

  def showJobView(jobInfo: JobInfo): Action[AnyContent] = authenticatedUserMessagesAction { implicit request: MessagesRequest[AnyContent] =>
    val jobMetadata = jobApplicationsService.resolveJobMetadata(jobInfo.jobId)
    Ok(views.html.authenticated.user.job.IndividualJobOverview(jobInfo, jobMetadata))
  }

  def deleteJob(jobId: Long): Action[AnyContent] = authenticatedUserAction { implicit request =>
    val userId = retrieveUserId(request)
    jobApplicationsService.deleteJob(userId, jobId)
    Redirect(routes.JobFeedController.showJobFeedHome(1))
  }

  def showDocumentsView(jobInfo: JobInfo): Action[AnyContent] = authenticatedUserMessagesAction { implicit request: MessagesRequest[AnyContent] =>
    val userId = retrieveUserId(request)
    val applicationFiles = jobApplicationsService.getApplicationsFiles(userId, jobInfo.jobId)
    Ok(views.html.authenticated.user.job.DocumentsToggleView(jobInfo, applicationFiles))
  }

  def generateCoverLetter(): Action[JsValue] = authenticatedUserMessagesAction(parse.json) { implicit request =>
    val fullName = retrieveUserName(request)
    val jobInfoValidation = (request.body \ "jobInfo").validate[JobInfo]

    jobInfoValidation match {
      case JsSuccess(jobInfo, _) =>
        val coverLetter = jobApplicationsService.generateCoverLetter(jobInfo, fullName)
        Ok(Json.obj("content" -> coverLetter.content))
      case _ =>
        BadRequest(Json.obj("error" -> "Invalid JSON format"))
    }
  }

  def generateResponse(): Action[JsValue] = authenticatedUserMessagesAction(parse.json) { implicit request =>
    val fullName = retrieveUserName(request)
    val questionValidation = (request.body \ "question").validate[String]
    val jobInfoValidation = (request.body \ "jobInfo").validate[JobInfo]

    (questionValidation, jobInfoValidation) match {
      case (JsSuccess(question, _), JsSuccess(jobInfo, _)) =>
        val response = jobApplicationsService.generateResponse(jobInfo, fullName, question)
        Ok(Json.obj("content" -> response.content))
      case _ =>
        BadRequest(Json.obj("error" -> "Invalid JSON format"))
    }
  }

  def generateResponseImprovement(): Action[JsValue] = authenticatedUserMessagesAction(parse.json) { implicit request =>
    val improvementsValidation = (request.body \ "improvements").validate[Seq[ResponseImprovement]]
    val customImprovementValidation = (request.body \ "customImprovement").validate[String]
    val responseValidation = (request.body \ "response").validate[String]
    val jobInfoValidation = (request.body \ "jobInfo").validate[JobInfo]

    (improvementsValidation, customImprovementValidation, responseValidation, jobInfoValidation) match {
      case (JsSuccess(improvements, _), JsSuccess(customImprovement, _), JsSuccess(response, _), JsSuccess(jobInfo, _)) =>
        val improvedResponse = jobApplicationsService.improveResponse(response, improvements, customImprovement)
        Ok(Json.obj("content" -> improvedResponse.content))
      case _ =>
        BadRequest(Json.obj("error" -> s"Invalid JSON format: $improvementsValidation, $customImprovementValidation, $responseValidation, $jobInfoValidation"))
    }
  }

  def saveCoverLetter(): Action[JsValue] = authenticatedUserMessagesAction(parse.json) { implicit request =>
    val userId = retrieveUserId(request)
    val jobId = (request.body \ "jobId").as[Long]
    val name = (request.body \ "name").as[String]
    val coverLetter = (request.body \ "coverLetter").as[String]
    jobApplicationsService.saveFile(userId, jobId, name, coverLetter, ApplicationFileType.CoverLetter)
    val jobInfo = jobApplicationsService.getJobById(userId, jobId)
    Redirect(routes.IndividualJobController.showDocumentsView(jobInfo))
  }

  def saveResponse(): Action[JsValue] = authenticatedUserMessagesAction(parse.json) { implicit request =>
    val userId = retrieveUserId(request)
    val jobId = (request.body \ "jobId").as[Long]
    val name = (request.body \ "name").as[String]
    val response = (request.body \ "response").as[String]
    jobApplicationsService.saveFile(userId, jobId, name, response, ApplicationFileType.Response)
    val jobInfo = jobApplicationsService.getJobById(userId, jobId)
    Redirect(routes.IndividualJobController.showDocumentsView(jobInfo))
  }

  private def retrieveUserId(request: RequestHeader): String = request.session.data(Global.SESSION_USER_ID)
  private def retrieveUserName(request: RequestHeader): String = request.session.data(Global.SESSION_USER_FULL_NAME)

}
