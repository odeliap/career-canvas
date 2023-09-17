package controllers

import authentication.{AuthenticatedUserAction, AuthenticatedUserMessagesAction}
import careercanvas.io.converter.Converters
import careercanvas.io.model.job._
import careercanvas.io.processor.resolvers.BaseJobInfoResolver
import model.Global
import service.JobApplicationsService
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json._
import utils.FormUtils

import javax.inject.Inject

class JobFeedController @Inject()(
  cc: MessagesControllerComponents,
  formUtils: FormUtils,
  jobApplicationsService: JobApplicationsService,
  authenticatedUserAction: AuthenticatedUserAction,
  authenticatedUserMessagesAction: AuthenticatedUserMessagesAction,
  baseJobInfoResolver: BaseJobInfoResolver
) extends MessagesAbstractController(cc) with Converters {

  private val logger = play.api.Logger(this.getClass)

  val jobPostForm: Form[JobPosting] = Form (
    mapping(
      "postUrl" -> nonEmptyText
        .verifying("Too few characters", s => formUtils.lengthIsGreaterThanNCharacters(s, 4))
        .verifying("Too many characters", s => formUtils.lengthIsLessThanNCharacters(s, 300))
    )(JobPosting.apply)(JobPosting.unapply)
  )

  def jobDetailsForm(baseJobInfo: BaseJobInfo): Form[UserProvidedJobDetails] = Form(
    mapping(
      "company" -> default(text, baseJobInfo.company)
        .verifying("too many chars", s => formUtils.lengthIsLessThanNCharacters(s, 40)),
      "jobTitle" -> default(text, baseJobInfo.jobTitle)
        .verifying("too many chars", s => formUtils.lengthIsLessThanNCharacters(s, 35)),
      "jobType" -> default(text, baseJobInfo.jobType.toString),
      "location" -> default(text, baseJobInfo.location),
      "salaryRange" -> default(text, baseJobInfo.salaryRange),
      "notes" -> optional(text)
        .verifying("too many chars", s => formUtils.lengthIsLessThanNCharacters(s.getOrElse(""), 1024)),
    )(UserProvidedJobDetails.apply)(UserProvidedJobDetails.unapply)
  )

  private val getPostInfoUrl = routes.JobFeedController.processJobPost()
  private val saveJobUrl = routes.JobFeedController.saveJob()

  def showJobFeedHome(): Action[AnyContent] = authenticatedUserMessagesAction { implicit request: MessagesRequest[AnyContent] =>
    val userId = retrieveUserId(request)
    val userJobs = retrieveUserJobs(userId)
    Ok(views.html.authenticated.user.feed.JobFeedDashboardView(jobPostForm, getPostInfoUrl, userJobs))
  }

  def processJobPost(): Action[AnyContent] = authenticatedUserMessagesAction { implicit request: MessagesRequest[AnyContent] =>
    val userId = retrieveUserId(request)
    val userJobs = retrieveUserJobs(userId)

    val errorFunction = { formWithErrors: Form[JobPosting] =>
      BadRequest(views.html.authenticated.user.feed.JobFeedDashboardView(formWithErrors, getPostInfoUrl, userJobs, showUrlModal = true))
        .flashing("error" -> "Error processing url")
    }

    val successFunction = { data: JobPosting =>
      val baseJobInfo = baseJobInfoResolver.resolve(data.postUrl)
      Redirect(routes.JobFeedController.showAddJobDetailsForm(baseJobInfo))
    }

    val formValidationResult: Form[JobPosting] = jobPostForm.bindFromRequest
    formValidationResult.fold(
      errorFunction,
      successFunction
    )
  }

  def showAddJobDetailsForm(baseJobInfo: BaseJobInfo): Action[AnyContent] = authenticatedUserMessagesAction { implicit request: MessagesRequest[AnyContent] =>
    val userId = retrieveUserId(request)
    val userJobs = retrieveUserJobs(userId)

    Ok(views.html.authenticated.user.feed.JobDetailsFormView(jobDetailsForm(baseJobInfo), saveJobUrl, userJobs, baseJobInfo))
      .withSession(
        request.session +
          ("company" -> baseJobInfo.company) +
          ("jobTitle" -> baseJobInfo.jobTitle) +
          ("postUrl" -> baseJobInfo.postUrl) +
          ("jobType" -> baseJobInfo.jobType.toString) +
          ("location" -> baseJobInfo.location) +
          ("salaryRange" -> baseJobInfo.salaryRange)
      )
  }

  def saveJob(): Action[AnyContent] = authenticatedUserMessagesAction { implicit request: MessagesRequest[AnyContent] =>
    val userId = retrieveUserId(request)
    val userJobs = retrieveUserJobs(userId)

    val baseJobInfo = BaseJobInfo(
      request.session.data("postUrl"),
      request.session.data("company"),
      request.session.data("jobTitle"),
      JobType.stringToEnum(request.session.data("jobType")),
      request.session.data("location"),
      request.session.data("salaryRange")
    )

    val errorFunction = { formWithErrors: Form[UserProvidedJobDetails] =>
      BadRequest(views.html.authenticated.user.feed.JobDetailsFormView(formWithErrors, saveJobUrl, userJobs, baseJobInfo))
        .flashing("error" -> "Error creating job frame")
        .withSession(request.session + ("company" -> baseJobInfo.company) + ("jobTitle" -> baseJobInfo.jobTitle) + ("postUrl" -> baseJobInfo.postUrl))
    }
    val successFunction = { data: UserProvidedJobDetails =>
      val userId = retrieveUserId(request)
      jobApplicationsService.createJob(data, baseJobInfo.postUrl, userId)
      Redirect(routes.JobFeedController.showJobFeedHome())
        .flashing("success" -> "Job frame added")
    }

    val formValidationResult: Form[UserProvidedJobDetails] = jobDetailsForm(baseJobInfo).bindFromRequest
    formValidationResult.fold(
      errorFunction,
      successFunction
    )
  }

  def removeJobDetails(): Action[AnyContent] = authenticatedUserMessagesAction { implicit request: MessagesRequest[AnyContent] =>
    Redirect(routes.JobFeedController.showJobFeedHome())
      .withSession(request.session -- Seq("company", "jobTitle", "postUrl"))
  }

  def starJob(jobId: Long): Action[AnyContent] = authenticatedUserAction { implicit request =>
    val userId = retrieveUserId(request)
    jobApplicationsService.starJob(userId, jobId)
    Redirect(routes.JobFeedController.showJobFeedHome())
  }

  def updateStatus(): Action[JsValue] = authenticatedUserMessagesAction(parse.json) { implicit request =>
    val userId = retrieveUserId(request)
    val jobId = (request.body \ "jobId").as[String]
    val status = (request.body \ "status").as[JobStatus]
    jobApplicationsService.updateStatus(userId, jobId, status)
    Ok(Json.obj("content" -> "status updated"))
  }

  private def retrieveUserId(request: RequestHeader): String = request.session.data(Global.SESSION_USER_ID)
  private def retrieveUserJobs(userId: String): Seq[JobInfo] = jobApplicationsService.getJobs(userId)

}
