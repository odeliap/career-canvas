package careercanvas.io.processor.resolvers

import careercanvas.io.model.job.{BaseJobInfo, JobType}
import careercanvas.io.processor.{CompletionResolver, OpenAiConnector}
import careercanvas.io.scraper.Scraper
import careercanvas.io.util.AwaitResult
import play.api.libs.json.{JsValue, Json}

import javax.inject.{Inject, Singleton}

@Singleton
class BaseJobInfoResolver @Inject()(
  scraper: Scraper,
  openAiConnector: OpenAiConnector,
) extends CompletionResolver[BaseJobInfo](openAiConnector)
  with AwaitResult {

  override def resolve(pageUrl: String, jobId: Long = 0L): BaseJobInfo = {
    val content = scraper.getPageContent(pageUrl)
    val completion = fetchCompletionWithRetry(content)
    completionToBaseJobInfo(completion, pageUrl)
  }

  private def completionToBaseJobInfo(completion: Option[String], pageUrl: String): BaseJobInfo = {
    completion match {
      case Some(completionStr) =>
        val json = Json.parse(completionStr)
        val company = (json \ "company").asOpt[String].getOrElse("Unable to resolve")
        val jobTitle = (json \ "jobTitle").asOpt[String].getOrElse("Unable to resolve")
        val jobTypeStr = (json \ "jobType").asOpt[String].getOrElse("Unknown")
        val jobType = JobType.stringToEnum(jobTypeStr)
        val location = (json \ "location").asOpt[String].getOrElse("Unable to resolve")
        val salaryRange = (json \ "salaryRange").asOpt[String].getOrElse("Unable to resolve")

        BaseJobInfo(pageUrl, company, jobTitle, jobType, location, salaryRange)
      case None => BaseJobInfo(pageUrl, unresolvedDefaultStr, unresolvedDefaultStr, JobType.default, unresolvedDefaultStr, unresolvedDefaultStr)
    }
  }

  override def isValidCompletion(json: JsValue): Boolean = {
    val fields = Seq("company", "jobTitle", "jobType", "location", "salaryRange")
    fields.forall(field => (json \ field).isDefined) &&
      JobType.values.map(_.toString).contains((json \ "jobType").asOpt[String].getOrElse(""))
  }

  override def generatePrompt(content: String): String = {
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
       |Do not return any HTML. Do not include anything other than this result in your response.
       |Do not append "Answer" to the start of your solution. Return only the JSON.
       |
       |Job Posting:
       |$content
    """.stripMargin
  }


}
