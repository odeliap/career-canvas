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

  override def resolve(pageUrl: String): JobDescriptions = {
    val content = scraper.getPageContent(pageUrl)
    val completion = fetchCompletionWithRetry(content)
    completionToJobDescriptions(completion)
  }

  private def completionToJobDescriptions(completion: Option[String]): JobDescriptions = {
    completion match {
      case Some(completionStr) => val json = Json.parse(completionStr)

        val about = (json \ "about").asOpt[String].getOrElse("Unable to resolve about")
        val requirements = (json \ "requirements").asOpt[String].getOrElse("Unable to resolve requirements")
        val techStack = (json \ "techStack").asOpt[String].getOrElse("Unable to resolve tech stack")

        JobDescriptions(0L, about, requirements, techStack)
      case None => JobDescriptions(0L, "Unable to resolve about", "Unable to resolve requirements", "Unable to resolve tech stack")
    }
  }

  override def isValidCompletion(json: JsValue): Boolean = {
    (json \ "about").isDefined && (json \ "requirements").isDefined && (json \ "techStack").isDefined
  }

  override def generatePrompt(content: String): String = {
    s"""Resolve an about field, the job requirements, and the technical stack from the following job post,
       |and return it in JSON format. The expected JSON structure is:
       |{
       |  "about": "About Field (max 1024 characters)",
       |  "requirements": "Job Requirements (max 1024 characters)"
       |  "techStack": "Technical Stack (max 1024 characters)"
       |}
       |Do not return any HTML. Do not include anything other than this result in your response.
       |Do not append "Answer" to the start of your solution. Return only the JSON.
       |If you cannot resolve one of these fields, use the value "Unable to resolve".
       |
       |Job Posting:
       |$content
    """.stripMargin
  }

}
