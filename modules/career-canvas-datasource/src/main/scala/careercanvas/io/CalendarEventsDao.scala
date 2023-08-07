package careercanvas.io

import careercanvas.io.model.{CalendarEvent, UpdateCalendarEvent}

import java.sql.Timestamp
import scala.concurrent.Future

trait CalendarEventsDao {

  def add(event: CalendarEvent): Future[CalendarEvent]

  def edit(updateCalendarEvent: UpdateCalendarEvent): Future[Unit]

  def getById(eventId: Long): Future[CalendarEvent]

  def findAllEvents(userId: Long): Future[Seq[CalendarEvent]]

  def findEventsInDateRange(userId: Long, start: Timestamp, end: Timestamp): Future[Seq[CalendarEvent]]

  def delete(eventId: Long): Future[Unit]

}
