package model

import play.api.libs.json._
import play.api.libs.functional.syntax._
import java.util.Date

case class NewEvent(
  title: String,
  start: String,
  end: String,
  allDay: String
)

object NewEvent {
  implicit val newEventReads: Reads[NewEvent] = (
    (JsPath \ "title").read[String] and
      (JsPath \ "start").read[String] and
      (JsPath \ "end").read[String] and
      (JsPath \ "allDay").read[String]
    )(NewEvent.apply _)

}