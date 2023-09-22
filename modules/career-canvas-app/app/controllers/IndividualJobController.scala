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
    val jobDescriptions = jobApplicationsService.resolveJobDescriptions(jobInfo.jobId)
    Ok(views.html.authenticated.user.job.IndividualJobOverview(jobInfo, jobDescriptions))
  }

  def deleteJob(jobId: Long): Action[AnyContent] = authenticatedUserAction { implicit request =>
    val userId = retrieveUserId(request)
    jobApplicationsService.deleteJob(userId, jobId)
    Redirect(routes.JobFeedController.showJobFeedHome())
  }

  def showDocumentsView(jobInfo: JobInfo): Action[AnyContent] = authenticatedUserMessagesAction { implicit request: MessagesRequest[AnyContent] =>
    val userId = retrieveUserId(request)
    val applicationFileUrls = jobApplicationsService.getApplicationsFiles(userId, jobInfo.jobId)
    Ok(views.html.authenticated.user.job.DocumentsToggleView(jobInfo, applicationFileUrls))
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
