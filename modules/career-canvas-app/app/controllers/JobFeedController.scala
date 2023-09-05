package controllers

import authentication.{AuthenticatedUserAction, AuthenticatedUserMessagesAction}
import careercanvas.io.converter.Converters
import careercanvas.io.model.jobfeed.{BaseJobInfo, JobInfo, JobPosting, SortKey, UserProvidedJobDetails}
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
    val userId = request.session.data(Global.SESSION_USER_ID)
    val userJobs = jobApplicationsService.getJobs(userId)
    Ok(views.html.authenticated.user.jobfeed.JobFeedDashboardView(sortByForm, jobPostForm, getPostInfoUrl, userJobs))
  }

  def processJobPost(): Action[AnyContent] = authenticatedUserMessagesAction { implicit request: MessagesRequest[AnyContent] =>
    val userId = request.session.data(Global.SESSION_USER_ID)
    val userJobs = jobApplicationsService.getJobs(userId)

    val errorFunction = { formWithErrors: Form[JobPosting] =>
      BadRequest(views.html.authenticated.user.jobfeed.JobFeedDashboardView(sortByForm, formWithErrors, getPostInfoUrl, userJobs))
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
    val userId = request.session.data(Global.SESSION_USER_ID)
    val userJobs = jobApplicationsService.getJobs(userId)

    Ok(views.html.authenticated.user.jobfeed.JobDetailsFormView(sortByForm, jobDetailsForm(baseJobInfo), saveJobUrl, userJobs, baseJobInfo))
      .withSession(request.session + ("company" -> baseJobInfo.company) + ("jobTitle" -> baseJobInfo.jobTitle) + ("postUrl" -> baseJobInfo.postUrl))
  }

  def saveJob(): Action[AnyContent] = authenticatedUserMessagesAction { implicit request: MessagesRequest[AnyContent] =>
    val userId = request.session.data(Global.SESSION_USER_ID)
    val userJobs = jobApplicationsService.getJobs(userId)

    val baseJobInfo = BaseJobInfo(
      request.session.data("company"),
      request.session.data("jobTitle"),
      request.session.data("postUrl")
    )

    val errorFunction = { formWithErrors: Form[UserProvidedJobDetails] =>
      BadRequest(views.html.authenticated.user.jobfeed.JobDetailsFormView(sortByForm, formWithErrors, saveJobUrl, userJobs, baseJobInfo))
        .flashing("error" -> "Error creating job frame")
        .withSession(request.session + ("company" -> baseJobInfo.company) + ("jobTitle" -> baseJobInfo.jobTitle) + ("postUrl" -> baseJobInfo.postUrl))
    }
    val successFunction = { data: UserProvidedJobDetails =>
      val userId = request.session.data(Global.SESSION_USER_ID)
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
    val userId = request.session.data(Global.SESSION_USER_ID)
    val userJobs = jobApplicationsService.getJobs(userId)
    Ok(views.html.authenticated.user.jobfeed.JobFeedDashboardView(sortByForm, jobPostForm, getPostInfoUrl, userJobs))
      .withSession(request.session -- Seq("company", "jobTitle", "postUrl"))
  }

  def showJobView(jobInfo: JobInfo): Action[AnyContent] = authenticatedUserAction { implicit request =>
    Ok(views.html.authenticated.user.jobfeed.IndividualJobView(jobInfo))
  }

  def generateCoverLetter(jobInfo: JobInfo): Action[AnyContent] = authenticatedUserAction { implicit request =>
    val fullName = request.session.data(Global.SESSION_USER_FULL_NAME)
    val coverLetter = jobApplicationsService.generateCoverLetter(jobInfo, fullName)
    Ok(views.html.authenticated.user.jobfeed.CoverLetterDisplayView(jobInfo, coverLetter))
  }

  private def lengthIsLessThanNCharacters(s: String, n: Int): Boolean = {
    if (s.length < n) true else false
  }

}
