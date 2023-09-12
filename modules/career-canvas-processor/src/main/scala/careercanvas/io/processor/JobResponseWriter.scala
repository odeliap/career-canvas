package careercanvas.io.processor

import akka.actor.ActorSystem
import akka.stream.Materializer
import careercanvas.io.model.job.{ResponseImprovement, JobInfo, Response}
import careercanvas.io.scraper.Scraper
import careercanvas.io.util.AwaitResult
import io.cequence.openaiscala.service.{OpenAIService, OpenAIServiceFactory}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class JobResponseWriter @Inject()(
  scraper: Scraper,
  openAiConnector: OpenAiConnector
)(implicit ec: ExecutionContext) extends AwaitResult  {

  implicit val materializer: Materializer = Materializer(ActorSystem())

  val openAiService: OpenAIService = OpenAIServiceFactory()

  def generateCoverLetter(jobInfo: JobInfo, name: String): Response = {
    val task = "Generate a cover letter."
    generateResponse(jobInfo, name, task)
  }

  def improveResponse(coverLetter: String, improvements: Seq[ResponseImprovement], customImprovement: String): Response = {
    val improvementsPrompt = resolveImprovements(improvements, customImprovement)
    val fullPrompt = resolveResponseImprovementPrompt(coverLetter, improvementsPrompt)
    promptToResponse(fullPrompt)
  }

  def generateResponse(jobInfo: JobInfo, name: String, task: String): Response = {
    val jobPostContent = scraper.getPageContent(jobInfo.postUrl)
    val fullPrompt = resolveFullPrompt(name, jobInfo.company, jobInfo.jobTitle, jobPostContent, task)
    promptToResponse(fullPrompt)
  }

  private def promptToResponse(fullPrompt: String): Response = {
    val completion = openAiConnector.complete(fullPrompt)
    Response(completion)
  }

  private def resolveImprovements(improvements: Seq[ResponseImprovement], customImprovement: String): String = {
    val providedImprovementsPrompt = if (improvements.nonEmpty) s"\nMake improvements to the following areas: ${improvements.mkString(", ")}" else ""
    val customImprovementPrompt = if (customImprovement.nonEmpty) s"\nMake the following improvement: $customImprovement" else ""
    providedImprovementsPrompt + customImprovementPrompt
  }

  private def resolveFullPrompt(name: String, company: String, jobTitle: String, content: String, task: String): String = {
    s"""Given the following position details:
       |Applicant Name: $name
       |Job Title: $jobTitle
       |Company: $company
       |Job Description: $content
       |
       |Complete the following task: $task
       |
       |Please format the response with newline characters suitable for Scala code.
    """.stripMargin
  }

  private def resolveResponseImprovementPrompt(coverLetter: String, improvementPrompts: String): String = {
    s"""
       |Improve the following response:
       |
       |$coverLetter
       |
       |Please do so by doing the following:
       |${improvementPrompts}
       |""".stripMargin
  }

}
