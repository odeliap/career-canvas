package controllers

import careercanvas.io.model.JobStatus
import careercanvas.io.model._
import model.Global
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import javax.inject.Inject

class JobFeedController @Inject()(
  cc: MessagesControllerComponents
) extends MessagesAbstractController(cc) {

  private val logger = play.api.Logger(this.getClass)

  val jobPostForm: Form[JobPosting] = Form (
    mapping(
      "postUrl" -> nonEmptyText
    )(JobPosting.apply)(JobPosting.unapply)
  )

  val jobDetailsForm: Form[UserProvidedJobDetails] = Form(
    mapping(
      "status" -> nonEmptyText,
      "interviewRound" -> optional(number),
      "dateApplied" -> optional(sqlTimestamp),
      "notes" -> optional(text)
    )(UserProvidedJobDetails.apply)(UserProvidedJobDetails.unapply)
  )

  private val getPostInfoUrl = routes.JobFeedController.processJobPost
  private def saveJobUrl(baseJobInfo: BaseJobInfo) = routes.JobFeedController.saveJob(baseJobInfo)

  def showJobFeedHome() = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.authenticated.user.jobFeedHome(jobPostForm, getPostInfoUrl))
  }

  def processJobPost() = Action { implicit request: MessagesRequest[AnyContent] =>
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

  def showAddJobDetailsForm(baseJobInfo: BaseJobInfo) = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.authenticated.user.addJobDetails(jobDetailsForm, saveJobUrl(baseJobInfo)))
  }

  def saveJob(baseJobInfo: BaseJobInfo) = Action { implicit request: MessagesRequest[AnyContent] =>
    val errorFunction = { formWithErrors: Form[UserProvidedJobDetails] =>
      BadRequest(views.html.authenticated.user.addJobDetails(formWithErrors, saveJobUrl(baseJobInfo)))
    }

    val successFunction = { data: UserProvidedJobDetails =>
      val jobInfo = JobInfo(
        jobId = 0L,
        userId = request.session.get(Global.SESSION_USER_ID).get.toLong,
        company = baseJobInfo.company,
        jobTitle = baseJobInfo.jobTitle,
        postUrl = baseJobInfo.postUrl,
        status = JobStatus.stringToEnum(data.status),
        appSubmissionDate = data.appSubmissionDate,
        interviewRound = data.interviewRound,
        notes = data.notes
      ) // TODO: save this information to the jobs database
      Redirect(routes.JobFeedController.showJobFeedHome())
    }
    val formValidationResult: Form[UserProvidedJobDetails] = jobDetailsForm.bindFromRequest
    formValidationResult.fold(
      errorFunction,
      successFunction
    )
  }

}
