package careercanvas.io.processor

import akka.actor.ActorSystem
import akka.stream.Materializer
import careercanvas.io.util.AwaitResult
import io.cequence.openaiscala.service.{OpenAIService, OpenAIServiceFactory}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class OpenAiConnector @Inject()()(implicit ec: ExecutionContext) extends AwaitResult{

  implicit val materializer: Materializer = Materializer(ActorSystem())

  val openAiService: OpenAIService = OpenAIServiceFactory()

  def complete(prompt: String): String = {
    openAiService
      .createCompletion(prompt)
      .map { completion =>
        completion.choices.headOption.map(_.text).getOrElse("")
      }
      .waitForResult
  }

}
