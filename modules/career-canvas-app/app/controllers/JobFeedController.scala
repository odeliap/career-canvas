package controllers

import authentication.AuthenticatedUserMessagesAction
import careercanvas.io.converter.Converters
import careercanvas.io.model._
import careercanvas.io.processor.BaseJobInfoResolver
import model.{Global, SortByForm}
import service.JobApplicationsService
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import javax.inject.Inject

// TODO: cache user jobs instead of making call to db every time
class JobFeedController @Inject()(
  cc: MessagesControllerComponents,
  jobApplicationsService: JobApplicationsService,
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
    )(JobPosting.apply)(JobPosting.unapply)
  )

  def jobDetailsForm(baseJobInfo: BaseJobInfo): Form[UserProvidedJobDetails] = Form(
    mapping(
      "company" -> default(text, baseJobInfo.company),
      "jobTitle" -> default(text, baseJobInfo.jobTitle),
      "status" -> text,
      "interviewRound" -> optional(number),
      "notes" -> optional(text)
    )(UserProvidedJobDetails.apply)(UserProvidedJobDetails.unapply)
  )

  private val getPostInfoUrl = routes.JobFeedController.processJobPost()
  private val saveJobUrl = routes.JobFeedController.saveJob()

  def showJobFeedHome(): Action[AnyContent] = authenticatedUserMessagesAction { implicit request: MessagesRequest[AnyContent] =>
    val userId = request.session.data(Global.SESSION_USER_ID)
    val userJobs = jobApplicationsService.getJobs(userId)
    Ok(views.html.authenticated.user.jobfeed.jobFeedHome(sortByForm, jobPostForm, getPostInfoUrl, userJobs))
  }

  def processJobPost(): Action[AnyContent] = authenticatedUserMessagesAction { implicit request: MessagesRequest[AnyContent] =>
    val userId = request.session.data(Global.SESSION_USER_ID)
    val userJobs = jobApplicationsService.getJobs(userId)

    val errorFunction = { formWithErrors: Form[JobPosting] =>
      BadRequest(views.html.authenticated.user.jobfeed.jobFeedHome(sortByForm, formWithErrors, getPostInfoUrl, userJobs))
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

    Ok(views.html.authenticated.user.jobfeed.addJobDetails(sortByForm, jobDetailsForm(baseJobInfo), saveJobUrl, userJobs, baseJobInfo))
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
      BadRequest(views.html.authenticated.user.jobfeed.addJobDetails(sortByForm, formWithErrors, saveJobUrl, userJobs, baseJobInfo))
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
    Ok(views.html.authenticated.user.jobfeed.jobFeedHome(sortByForm, jobPostForm, getPostInfoUrl, userJobs))
      .withSession(request.session -- Seq("company", "jobTitle", "postUrl"))
  }

  def showJobView(jobInfo: JobInfo): Action[AnyContent] = authenticatedUserMessagesAction { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.authenticated.user.jobfeed.jobView(jobInfo))
  }

  def generateCoverLetter(jobInfo: JobInfo): Action[AnyContent] = authenticatedUserMessagesAction { implicit request: MessagesRequest[AnyContent] =>
    val fullName = request.session.data(Global.SESSION_USER_FULL_NAME)
    val coverLetter = jobApplicationsService.generateCoverLetter(jobInfo, fullName)
    Ok(views.html.authenticated.user.jobfeed.showCoverLetter(jobInfo, coverLetter))
  }

}
