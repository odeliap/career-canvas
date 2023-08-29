package careercanvas.io.model.calendar

import java.sql.Timestamp

case class CalendarEvent(
                          userId: Long,
                          eventId: Long,
                          title: String,
                          allDay: Boolean,
                          start: Timestamp,
                          end: Timestamp,
                          endsSameDay: Boolean
                        ) {

  def patch(updateCalendarEvent: UpdateCalendarEvent): CalendarEvent = {
    this.copy(
      title = updateCalendarEvent.title.getOrElse(this.title),
      allDay = updateCalendarEvent.allDay.getOrElse(this.allDay),
      start = updateCalendarEvent.start.getOrElse(this.start),
      end = updateCalendarEvent.end.getOrElse(this.end),
      endsSameDay = updateCalendarEvent.endsSameDay.getOrElse(this.endsSameDay)
    )
  }

}
