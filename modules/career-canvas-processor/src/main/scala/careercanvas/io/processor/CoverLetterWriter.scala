package careercanvas.io.processor

import akka.actor.ActorSystem
import akka.stream.Materializer
import careercanvas.io.model.CoverLetter
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

  def generateCoverLetter(pageUrl: String): CoverLetter = {
    val content = scraper.getPageContent(pageUrl)
    val prompt = resolvePrompt(content)
    val completion = openAiConnector.complete(prompt)
    completionToCoverLetter(completion)
  }

  private def completionToCoverLetter(completion: String): CoverLetter = {
    CoverLetter(completion)
  }

  private def resolvePrompt(content: String): String = {
    s"""Write me a cover letter for the following job description post:
       |$content
    """.stripMargin
  }

}
