package careercanvas.io.processor

import careercanvas.io.model.job.{BaseJobInfo, JobType}
import careercanvas.io.scraper.Scraper
import careercanvas.io.util.AwaitResult
import play.api.libs.json.{JsValue, Json}

import javax.inject.{Inject, Singleton}
import scala.annotation.tailrec

@Singleton
class BaseJobInfoResolver @Inject()(
  scraper: Scraper,
  openAiConnector: OpenAiConnector,
) extends AwaitResult {

  def resolve(pageUrl: String): BaseJobInfo = {
    val content = scraper.getPageContent(pageUrl)
    val completion = fetchCompletionWithRetry(content)
    completionToBaseJobInfo(completion, pageUrl)
  }

  @tailrec
  private def fetchCompletionWithRetry(content: String, retries: Int = 3): String = {
    val prompt = generatePrompt(content)
    val completion = openAiConnector.complete(prompt)

    val json = Json.parse(completion)
    if (isValidCompletion(json) || retries <= 0) {
      completion
    } else {
      fetchCompletionWithRetry(content, retries - 1)
    }
  }

  private def completionToBaseJobInfo(completion: String, pageUrl: String): BaseJobInfo = {
    val json = Json.parse(completion)

    val company = (json \ "company").asOpt[String].getOrElse("Unable to resolve")
    val jobTitle = (json \ "jobTitle").asOpt[String].getOrElse("Unable to resolve")
    val jobTypeStr = (json \ "jobType").asOpt[String].getOrElse("Unknown")
    val jobType = JobType.stringToEnum(jobTypeStr)
    val location = (json \ "location").asOpt[String].getOrElse("Unable to resolve")
    val salaryRange = (json \ "salaryRange").asOpt[String].getOrElse("Unable to resolve")

    BaseJobInfo(pageUrl, company, jobTitle, jobType, location, salaryRange)
  }

  private def generatePrompt(content: String): String = {
    s"""Based on the job post provided, extract the relevant information and return it in JSON format.
       |The expected JSON structure is:
       |{
       |  "company": "Company Name",
       |  "jobTitle": "Job Title",
       |  "jobType": "Job Type (strictly use one of: FullTime, Contract, PartTime, Internship, Temporary)",
       |  "location": "Location",
       |  "salaryRange": "Salary Range"
       |}
       |
       |For fields you cannot determine, use the value "Unable to resolve", except for "jobType" which should be one of the mentioned types.
       |
       |Job Posting:
       |$content
    """.stripMargin
  }

  private def isValidCompletion(json: JsValue): Boolean = {
    val fields = Seq("company", "jobTitle", "jobType", "location", "salaryRange")
    fields.forall(field => (json \ field).isDefined) &&
      JobType.values.map(_.toString).contains((json \ "jobType").asOpt[String].getOrElse(""))
  }


}
