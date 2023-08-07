package careercanvas.io.model

import java.sql.Timestamp

case class UpdateCalendarEvent(
  eventId: Long,
  title: Option[String],
  allDay: Option[Boolean],
  start: Option[Timestamp],
  end: Option[Timestamp],
  endsSameDay: Option[Boolean]
)
