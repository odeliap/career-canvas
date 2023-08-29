package service

import careercanvas.io.CalendarEventsDao
import careercanvas.io.model.calendar
import careercanvas.io.model.calendar.{CalendarEvent, UpdateCalendarEvent}
import careercanvas.io.util.AwaitResult
import model.calendar.NewEvent

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject
import scala.concurrent.ExecutionContext

class CalendarEventsService @Inject() (
  calendarEventsDao: CalendarEventsDao
)(implicit ec: ExecutionContext)
  extends AwaitResult {

  def endsSameDay(start: String, end: String): Boolean = {
    val dateFormat = new SimpleDateFormat("yyyyMMdd")
    dateFormat.format(start).equals(dateFormat.format(end))
  }

  def addEvent(userId: String, newEvent: NewEvent): CalendarEvent = {
    val dateFormatter = new SimpleDateFormat("dd.MMM.yyyy HH:mm:ss")
    val event = calendar.CalendarEvent(
      userId.toLong,
      0L,
      newEvent.title,
      newEvent.allDay != null,
      Timestamp.from(dateFormatter.parse(newEvent.start).toInstant),
      Timestamp.from(dateFormatter.parse(newEvent.end).toInstant),
      endsSameDay(newEvent.start, newEvent.end)
    )
    calendarEventsDao.add(event).waitForResult
  }

  def updateEvent(updateCalendarEvent: UpdateCalendarEvent): Unit = {
    calendarEventsDao.edit(updateCalendarEvent).waitForResult
  }

  def getEventInfo(eventId: Long): CalendarEvent = {
    calendarEventsDao.getById(eventId).waitForResult
  }

  def findAllEvents(userId: String): Seq[CalendarEvent] = {
    calendarEventsDao.findAllEvents(userId.toLong).waitForResult
  }

  def findEventsInDateRange(userId: String, start: Long, end: Long): Seq[CalendarEvent] = {
    val startDate = Timestamp.from(new Date(start * 1000).toInstant)
    val endDate = Timestamp.from(new Date(end * 1000).toInstant)
    calendarEventsDao.findEventsInDateRange(userId.toLong, startDate, endDate).waitForResult
  }

  def deleteEvent(eventId: Long): Unit = {
    calendarEventsDao.delete(eventId).waitForResult
  }

}
