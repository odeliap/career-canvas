package careercanvas.io.processor.resolvers

import careercanvas.io.model.job.JobDescriptions
import careercanvas.io.processor.{CompletionResolver, OpenAiConnector}
import careercanvas.io.scraper.Scraper
import careercanvas.io.util.AwaitResult
import play.api.libs.json._

import javax.inject._

class JobDescriptionsResolver @Inject()(
  scraper: Scraper,
  openAiConnector: OpenAiConnector
) extends CompletionResolver[JobDescriptions](openAiConnector)
  with AwaitResult {

  override def resolve(pageUrl: String, jobId: Long): JobDescriptions = {
    val content = scraper.getPageContent(pageUrl)
    val completion = fetchCompletionWithRetry(content)
    completionToJobDescriptions(jobId, completion)
  }

  private def completionToJobDescriptions(jobId: Long, completion: Option[String]): JobDescriptions = {
    completion match {
      case Some(completionStr) => val json = Json.parse(completionStr)

        val jobDescription = (json \ "jobDescription").asOpt[String].getOrElse("Unable to resolve description")
        val companyDescription = (json \ "companyDescription").asOpt[String].getOrElse("Unable to resolve description")

        JobDescriptions(jobId, jobDescription, companyDescription)
      case None => JobDescriptions(jobId, unresolvedDefaultStr, unresolvedDefaultStr)
    }
  }

  override def isValidCompletion(json: JsValue): Boolean = {
    (json \ "jobDescription").isDefined && (json \ "companyDescription").isDefined
  }

  override def generatePrompt(content: String): String = {
    s"""Extract the job description and company description from the following job post,
       |and return it in JSON format. The expected JSON structure is:
       |{
       |  "jobDescription": "Job Description (max 1024 characters)",
       |  "companyDescription": "Company Description (max 1024 characters)"
       |}
       |Do not return any HTML. Do not include anything other than this result in your response.
       |Do not append "Answer" to the start of your solution. Return only the JSON.
       |If you cannot resolve one of these fields, use the value "Unable to resolve description".
       |
       |Job Posting:
       |$content
    """.stripMargin
  }

}
