package careercanvas.io.processor

import careercanvas.io.model.job.CompletionResolvedInfo
import play.api.libs.json.{JsValue, Json}

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

abstract class CompletionResolver[T <: CompletionResolvedInfo](openAiConnector: OpenAiConnector) {

  val unresolvedDefaultStr: String = "Unable to Resolve"

  def resolve(pageUrl: String, jobId: Long = 0L): T

  def generatePrompt(content: String): String

  def isValidCompletion(value: JsValue): Boolean

  final def fetchCompletionWithRetry(content: String, retries: Int = 3): Option[String] = {
    @tailrec
    def fetchWithRetries(retriesLeft: Int): Option[String] = {
      if (retriesLeft <= 0) {
        None
      } else {
        val prompt = generatePrompt(content)
        val completionResult = Try(openAiConnector.complete(prompt))

        completionResult match {
          case Success(completion) =>
            val jsonParsingResult = Try(Json.parse(completion))

            jsonParsingResult match {
              case Success(json) =>
                if (isValidCompletion(json)) {
                  Some(completion)
                } else {
                  fetchWithRetries(retriesLeft - 1)
                }

              case Failure(_) =>
                fetchWithRetries(retriesLeft - 1)
            }

          case Failure(_) =>
            fetchWithRetries(retriesLeft - 1)
        }
      }
    }

    fetchWithRetries(retries)
  }

}
