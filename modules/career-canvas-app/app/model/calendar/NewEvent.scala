package model.calendar

import play.api.libs.functional.syntax._
import play.api.libs.json._

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