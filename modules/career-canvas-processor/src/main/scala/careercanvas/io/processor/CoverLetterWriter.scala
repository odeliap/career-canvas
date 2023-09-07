package careercanvas.io.processor

import akka.actor.ActorSystem
import akka.stream.Materializer
import careercanvas.io.model.feed.{CoverLetter, JobInfo}
import careercanvas.io.scraper.Scraper
import careercanvas.io.util.AwaitResult
import io.cequence.openaiscala.service.{OpenAIService, OpenAIServiceFactory}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class CoverLetterWriter @Inject() (
  scraper: Scraper,
  openAiConnector: OpenAiConnector
)(implicit ec: ExecutionContext) extends AwaitResult  {

  implicit val materializer: Materializer = Materializer(ActorSystem())

  val openAiService: OpenAIService = OpenAIServiceFactory()

  def generateCoverLetter(jobInfo: JobInfo, name: String): CoverLetter = {
    val content = scraper.getPageContent(jobInfo.postUrl)
    val prompt = resolvePrompt(name, jobInfo.company, jobInfo.jobTitle, content)
    val completion = openAiConnector.complete(prompt)
    completionToCoverLetter(completion)
  }

  private def completionToCoverLetter(completion: String): CoverLetter = {
    CoverLetter(completion)
  }

  private def resolvePrompt(name: String, company: String, jobTitle: String, content: String): String = {
    s"""Write a cover letter for the following position:
       |Applicant Name: $name
       |Job Title: $jobTitle
       |Company: $company
       |Job Description: $content
       |
       |Please format the response with newline characters suitable for Scala code.
    """.stripMargin
  }

}
