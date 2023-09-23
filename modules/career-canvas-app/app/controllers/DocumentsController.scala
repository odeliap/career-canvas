package controllers

import authentication.AuthenticatedUserMessagesAction
import careercanvas.io.converter.Converters
import careercanvas.io.model.job.{ApplicationFile, ApplicationFileType, JobInfo, ResponseImprovement}
import model.Global
import play.api.libs.json.{JsSuccess, JsValue, Json}
import play.api.mvc.{Action, AnyContent, MessagesAbstractController, MessagesControllerComponents, MessagesRequest, RequestHeader}
import service.{DocumentsService, JobApplicationsService}

import javax.inject.Inject

class DocumentsController @Inject() (
  cc: MessagesControllerComponents,
  documentsService: DocumentsService,
  jobApplicationsService: JobApplicationsService,
  authenticatedUserMessagesAction: AuthenticatedUserMessagesAction
) extends MessagesAbstractController(cc) with Converters {

  def showDocumentsView(jobInfo: JobInfo): Action[AnyContent] = authenticatedUserMessagesAction { implicit request: MessagesRequest[AnyContent] =>
    val userId = retrieveUserId(request)
    val resumes = jobApplicationsService.getResumes(userId)
    val applicationFileUrls = documentsService.getApplicationsFiles(userId, jobInfo.jobId)
    Ok(views.html.authenticated.user.job.DocumentsToggleView(jobInfo, resumes, applicationFileUrls))
  }

  def generateCoverLetter(): Action[JsValue] = authenticatedUserMessagesAction(parse.json) { implicit request =>
    val jobInfoValidation = (request.body \ "jobInfo").validate[JobInfo]
    val resumeValidation = (request.body \ "resume").validate[String]

    (jobInfoValidation, resumeValidation) match {
      case (JsSuccess(jobInfo, _), JsSuccess(resumeVersion, _)) =>
        val coverLetter = documentsService.generateCoverLetter(jobInfo, resumeVersion)
        Ok(Json.obj("content" -> coverLetter.content))
      case _ =>
        BadRequest(Json.obj("error" -> "Invalid JSON format"))
    }
  }

  def generateResponse(): Action[JsValue] = authenticatedUserMessagesAction(parse.json) { implicit request =>
    val questionValidation = (request.body \ "question").validate[String]
    val jobInfoValidation = (request.body \ "jobInfo").validate[JobInfo]
    val resumeValidation = (request.body \ "resume").validate[String]

    (questionValidation, jobInfoValidation, resumeValidation) match {
      case (JsSuccess(question, _), JsSuccess(jobInfo, _), JsSuccess(resumeVersion, _)) =>
        val response = documentsService.generateResponse(jobInfo, resumeVersion, question)
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
        val improvedResponse = documentsService.improveResponse(response, improvements, customImprovement)
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
    documentsService.saveFile(userId, jobId, name, coverLetter, ApplicationFileType.CoverLetter)
    val jobInfo = jobApplicationsService.getJobById(userId, jobId)
    Redirect(routes.DocumentsController.showDocumentsView(jobInfo))
  }

  def saveResponse(): Action[JsValue] = authenticatedUserMessagesAction(parse.json) { implicit request =>
    val userId = retrieveUserId(request)
    val jobId = (request.body \ "jobId").as[Long]
    val name = (request.body \ "name").as[String]
    val response = (request.body \ "response").as[String]
    documentsService.saveFile(userId, jobId, name, response, ApplicationFileType.CustomResponse)
    val jobInfo = jobApplicationsService.getJobById(userId, jobId)
    Redirect(routes.DocumentsController.showDocumentsView(jobInfo))
  }

  def deleteResponse(): Action[JsValue] = authenticatedUserMessagesAction(parse.json) { implicit request =>
    val userId = retrieveUserId(request)
    val jobId = (request.body \ "jobId").as[Long]
    val fileId = (request.body \ "fileId").as[Long]
    documentsService.deleteResponse(userId, jobId, fileId)
    val jobInfo = jobApplicationsService.getJobById(userId, jobId)
    Redirect(routes.DocumentsController.showDocumentsView(jobInfo))
  }


  def editDocument(applicationFile: ApplicationFile): Action[AnyContent] = authenticatedUserMessagesAction { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.authenticated.user.job.EditDocument(applicationFile))
  }

  private def retrieveUserId(request: RequestHeader): String = request.session.data(Global.SESSION_USER_ID)

}
