package controllers

import careercanvas.io.model._
import service.JobApplicationsService
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import javax.inject.Inject

// TODO: make these methods authenticated user actions
class JobFeedController @Inject()(
  cc: MessagesControllerComponents,
  jobApplicationsService: JobApplicationsService
) extends MessagesAbstractController(cc) {

  private val logger = play.api.Logger(this.getClass)

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
      "dateApplied" -> optional(sqlTimestamp),
      "notes" -> optional(text)
    )(UserProvidedJobDetails.apply)(UserProvidedJobDetails.unapply)
  )

  private val getPostInfoUrl = routes.JobFeedController.processJobPost()
  private val saveJobUrl = routes.JobFeedController.saveJob()

  def showJobFeedHome(): Action[AnyContent] = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.authenticated.user.jobFeedHome(jobPostForm, getPostInfoUrl))
  }

  def processJobPost(): Action[AnyContent] = Action { implicit request: MessagesRequest[AnyContent] =>
    val errorFunction = { formWithErrors: Form[JobPosting] =>
      BadRequest(views.html.authenticated.user.jobFeedHome(formWithErrors, getPostInfoUrl))
    }

    val successFunction = { data: JobPosting =>
      val baseJobInfo = BaseJobInfo(
        "company", // # TODO: get me from calling scraping api
        "jobTitle", // # TODO: get me from calling scraping api
        data.postUrl
      )
      Redirect(routes.JobFeedController.showAddJobDetailsForm(baseJobInfo))
    }

    val formValidationResult: Form[JobPosting] = jobPostForm.bindFromRequest
    formValidationResult.fold(
      errorFunction,
      successFunction
    )
  }

  def showAddJobDetailsForm(baseJobInfo: BaseJobInfo): Action[AnyContent] = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.authenticated.user.addJobDetails(jobDetailsForm(baseJobInfo), saveJobUrl))
      .withSession("company" -> baseJobInfo.company, "jobTitle" -> baseJobInfo.jobTitle, "postUrl" -> baseJobInfo.postUrl)
  }

  def saveJob(): Action[AnyContent] = Action { implicit request: MessagesRequest[AnyContent] =>
    val baseJobInfo = BaseJobInfo(
      request.session.get("company").get,
      request.session.get("jobTitle").get,
      request.session.get("postUrl").get
    )

    val errorFunction = { formWithErrors: Form[UserProvidedJobDetails] =>
      BadRequest(views.html.authenticated.user.addJobDetails(formWithErrors, saveJobUrl))
        .withSession("company" -> baseJobInfo.company, "jobTitle" -> baseJobInfo.jobTitle, "postUrl" -> baseJobInfo.postUrl)
        .flashing("error" -> "Error creating job frame")
    }
    val successFunction = { data: UserProvidedJobDetails =>
      jobApplicationsService.createJob(data, baseJobInfo.postUrl, "1") // TODO: fix me to actual user id from session
      Redirect(routes.JobFeedController.showJobFeedHome())
        .flashing("success" -> "Job frame added")
    }

    val formValidationResult: Form[UserProvidedJobDetails] = jobDetailsForm(baseJobInfo).bindFromRequest
    formValidationResult.fold(
      errorFunction,
      successFunction
    )
  }

  def removeJobDetails(): Action[AnyContent] = Action { implicit request: MessagesRequest[AnyContent] =>
    request.session -- Seq("company", "jobTitle", "postUrl")
    Ok(views.html.authenticated.user.jobFeedHome(jobPostForm, getPostInfoUrl))
  }

}
