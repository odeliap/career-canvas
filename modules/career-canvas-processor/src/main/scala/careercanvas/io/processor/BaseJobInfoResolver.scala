package careercanvas.io.processor

import akka.actor.ActorSystem
import akka.stream.Materializer
import careercanvas.io.model.BaseJobInfo
import careercanvas.io.scraper.Scraper
import careercanvas.io.util.AwaitResult
import io.cequence.openaiscala.service.{OpenAIService, OpenAIServiceFactory}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class BaseJobInfoResolver @Inject()(
  scraper: Scraper
)(implicit ec: ExecutionContext) extends AwaitResult {

  implicit val materializer: Materializer = Materializer(ActorSystem())

  val openAiService: OpenAIService = OpenAIServiceFactory()

  def resolve(pageUrl: String): BaseJobInfo = {
    val title = scraper.getPageTitle(pageUrl)
    val prompt = resolvePrompt(title.title)
    val completion = complete(prompt)
    completionToBaseJobInfo(completion, pageUrl)
  }

  private def complete(prompt: String): String = {
    openAiService
      .createCompletion(prompt)
      .map { completion =>
        completion.choices.head.text
      }
      .waitForResult
  }

  private def completionToBaseJobInfo(completion: String, pageUrl: String): BaseJobInfo = {
    val args = completion.split("//////")
    val company = args(0)
    val jobTitle = args(1)
    BaseJobInfo(company, jobTitle, pageUrl)
  }

  private def resolvePrompt(title: String): String = {
    s"""Extract the company name and job title from the following job post title,
       |formatting the result like <company>//////<job-title>:
       |$title
    """.stripMargin
  }


}
