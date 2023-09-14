import careercanvas.io.model.job.JobDescriptions
import careercanvas.io.processor.OpenAiConnector
import careercanvas.io.scraper.Scraper
import careercanvas.io.util.AwaitResult

import javax.inject._
import play.api.libs.json._

import scala.annotation.tailrec

class JobDescriptionsResolver @Inject()(
  scraper: Scraper,
  openAiConnector: OpenAiConnector
) extends AwaitResult {

  def resolve(jobId: Long, pageUrl: String): JobDescriptions = {
    val content = scraper.getPageContent(pageUrl)
    val completion = fetchCompletionWithRetry(content)
    completionToJobDescriptions(jobId, completion)
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

  private def isValidCompletion(json: JsValue): Boolean = {
    (json \ "jobDescription").isDefined && (json \ "companyDescription").isDefined
  }

  private def generatePrompt(content: String): String = {
    s"""Extract the job description and company description from the following job post,
       |and return it in JSON format. The expected JSON structure is:
       |{
       |  "jobDescription": "Job Description (max 1024 characters)",
       |  "companyDescription": "Company Description (max 1024 characters)"
       |}
       |Do not include anything other than this result in your response.
       |If you cannot resolve one of these fields, use the value "Unable to resolve description".
       |
       |Job Posting:
       |$content
    """.stripMargin
  }

  private def completionToJobDescriptions(jobId: Long, completion: String): JobDescriptions = {
    val json = Json.parse(completion)

    val jobDescription = (json \ "jobDescription").asOpt[String].getOrElse("Unable to resolve description")
    val companyDescription = (json \ "companyDescription").asOpt[String].getOrElse("Unable to resolve description")

    JobDescriptions(jobId, jobDescription, companyDescription)
  }

}
