package controllers

import authentication.{AuthenticatedUserAction, AuthenticatedUserMessagesAction}
import careercanvas.io.converter.Converters
import careercanvas.io.model.job._
import careercanvas.io.processor.BaseJobInfoResolver
import model.Global
import model.forms.SortByForm
import service.JobApplicationsService
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
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

  val sortByForm: Form[SortByForm] = Form (
    mapping(
      "sortKey" -> of[SortKey]
    )(SortByForm.apply)(SortByForm.unapply)
  )

  val jobPostForm: Form[JobPosting] = Form (
    mapping(
      "postUrl" -> nonEmptyText
        .verifying("too few chars", s => formUtils.lengthIsGreaterThanNCharacters(s, 4))
        .verifying("too many chars", s => formUtils.lengthIsLessThanNCharacters(s, 300))
    )(JobPosting.apply)(JobPosting.unapply)
  )

  def jobDetailsForm(baseJobInfo: BaseJobInfo): Form[UserProvidedJobDetails] = Form(
    mapping(
      "company" -> default(text, baseJobInfo.company)
        .verifying("too many chars", s => formUtils.lengthIsLessThanNCharacters(s, 40)),
      "jobTitle" -> default(text, baseJobInfo.jobTitle)
        .verifying("too many chars", s => formUtils.lengthIsLessThanNCharacters(s, 35)),
      "status" -> text,
      "interviewRound" -> optional(number),
      "notes" -> optional(text)
        .verifying("too many chars", s => formUtils.lengthIsLessThanNCharacters(s.getOrElse(""), 1024)),
    )(UserProvidedJobDetails.apply)(UserProvidedJobDetails.unapply)
  )

  private val getPostInfoUrl = routes.JobFeedController.processJobPost()
  private val saveJobUrl = routes.JobFeedController.saveJob()

  def showJobFeedHome(): Action[AnyContent] = authenticatedUserMessagesAction { implicit request: MessagesRequest[AnyContent] =>
    val userId = retrieveUserId(request)
    val userJobs = retrieveUserJobs(userId)
    Ok(views.html.authenticated.user.feed.JobFeedDashboardView(sortByForm, jobPostForm, getPostInfoUrl, userJobs.jobs, userJobs.hasNext))
  }

  def processJobPost(): Action[AnyContent] = authenticatedUserMessagesAction { implicit request: MessagesRequest[AnyContent] =>
    val userId = retrieveUserId(request)
    val userJobs = retrieveUserJobs(userId)

    val errorFunction = { formWithErrors: Form[JobPosting] =>
      BadRequest(views.html.authenticated.user.feed.JobFeedDashboardView(sortByForm, formWithErrors, getPostInfoUrl, userJobs.jobs, userJobs.hasNext))
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

    Ok(views.html.authenticated.user.feed.JobDetailsFormView(sortByForm, jobDetailsForm(baseJobInfo), saveJobUrl, userJobs.jobs, userJobs.hasNext, baseJobInfo))
      .withSession(request.session + ("company" -> baseJobInfo.company) + ("jobTitle" -> baseJobInfo.jobTitle) + ("postUrl" -> baseJobInfo.postUrl))
  }

  def saveJob(): Action[AnyContent] = authenticatedUserMessagesAction { implicit request: MessagesRequest[AnyContent] =>
    val userId = retrieveUserId(request)
    val userJobs = retrieveUserJobs(userId)

    val baseJobInfo = BaseJobInfo(
      request.session.data("company"),
      request.session.data("jobTitle"),
      request.session.data("postUrl")
    )

    val errorFunction = { formWithErrors: Form[UserProvidedJobDetails] =>
      BadRequest(views.html.authenticated.user.feed.JobDetailsFormView(sortByForm, formWithErrors, saveJobUrl, userJobs.jobs, userJobs.hasNext, baseJobInfo))
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
    val userId = retrieveUserId(request)
    val userJobs = retrieveUserJobs(userId)
    Ok(views.html.authenticated.user.feed.JobFeedDashboardView(sortByForm, jobPostForm, getPostInfoUrl, userJobs.jobs, userJobs.hasNext))
      .withSession(request.session -- Seq("company", "jobTitle", "postUrl"))
  }

  def starJob(jobId: Long): Action[AnyContent] = authenticatedUserAction { implicit request =>
    val userId = retrieveUserId(request)
    jobApplicationsService.starJob(userId, jobId)
    Redirect(routes.JobFeedController.showJobFeedHome())
  }

  def sortJobs() : Action[AnyContent] = authenticatedUserMessagesAction { implicit request: MessagesRequest[AnyContent] =>
    val userId = retrieveUserId(request)
    sortByForm.bindFromRequest.fold(
      formWithErrors => {
        val userJobs = retrieveUserJobs(userId)
        BadRequest(views.html.authenticated.user.feed.JobFeedDashboardView(formWithErrors, jobPostForm, getPostInfoUrl, userJobs.jobs, userJobs.hasNext))
      },
      sortFormData => {
        val userJobs = jobApplicationsService.getJobs(userId, sortFormData.sortKey)
        Ok(views.html.authenticated.user.feed.JobFeedDashboardView(sortByForm.fill(sortFormData), jobPostForm, getPostInfoUrl, userJobs.jobs, userJobs.hasNext))
      }
    )
  }

  private def retrieveUserId(request: RequestHeader): String = request.session.data(Global.SESSION_USER_ID)
  private def retrieveUserJobs(userId: String, sortKey: SortKey = SortKey.Company): JobsResult = jobApplicationsService.getJobs(userId, sortKey)

}
