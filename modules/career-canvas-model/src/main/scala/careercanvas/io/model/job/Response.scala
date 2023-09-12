package careercanvas.io.model.job

import play.api.libs.json.{Json, Reads}

case class Response(
  content: String
)

object Response {
  implicit val responseReads: Reads[Response] = Json.reads[Response]
}