package careercanvas.io.dao.components

import careercanvas.io.model.CalendarEvent
import slick.lifted.ProvenShape

import java.sql.Timestamp

trait CalendarEventsComponent {

  import careercanvas.io.dao.profile.CareerCanvasSlickProfile.api._

  val CalendarEventsQuery = TableQuery[CalendarEventsTable]

  class CalendarEventsTable(tag: Tag) extends Table[CalendarEvent](tag, "calendar_event") {

    def userId: Rep[Long] = column[Long]("user_id")
    def eventId: Rep[Long] = column[Long]("event_id", O.AutoInc)
    def title: Rep[String] = column[String]("title")
    def allDay: Rep[Boolean] = column[Boolean]("all_day")
    def start: Rep[Timestamp] = column[Timestamp]("start_timestamp")
    def end: Rep[Timestamp] = column[Timestamp]("end_timestamp")
    def endsSameDay: Rep[Boolean] = column[Boolean]("ends_same_day")

    def * : ProvenShape[CalendarEvent] = (
      userId,
      eventId,
      title,
      allDay,
      start,
      end,
      endsSameDay
    ) <> (CalendarEvent.tupled, CalendarEvent.unapply)

  }

}
