package careercanvas.io.processor

import careercanvas.io.model.job.{BaseJobInfo, JobType}
import careercanvas.io.scraper.Scraper
import careercanvas.io.util.AwaitResult
import play.api.libs.json.{JsValue, Json}

import javax.inject.{Inject, Singleton}

@Singleton
class BaseJobInfoResolver @Inject()(
  scraper: Scraper,
  openAiConnector: OpenAiConnector,
) extends AwaitResult {

  def resolve(pageUrl: String, retries: Int = 3): BaseJobInfo = {
    val content = scraper.getPageContent(pageUrl)
    val prompt = resolvePrompt(content)
    val completion = openAiConnector.complete(prompt)

    val json = Json.parse(completion)
    if (isResponseValid(json) || retries <= 0) {
      completionToBaseJobInfo(completion, pageUrl)
    } else {
      resolve(content, retries - 1)
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

  private def resolvePrompt(content: String): String = {
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

  private def isResponseValid(json: JsValue): Boolean = {
    val fields = Seq("company", "jobTitle", "jobType", "location", "salaryRange")
    fields.forall(field => (json \ field).isDefined) &&
      JobType.values.map(_.toString).contains((json \ "jobType").asOpt[String].getOrElse(""))
  }


}
