package service

import careercanvas.io.CalendarEventsDao
import careercanvas.io.model.{CalendarEvent, UpdateCalendarEvent}
import careercanvas.io.util.AwaitResult
import model.NewEvent

import java.sql.Timestamp
import java.text.SimpleDateFormat
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
    val dateFormatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss")
    val event = CalendarEvent(
      userId.toLong,
      0L,
      newEvent.title,
      newEvent.allDay != null,
      Timestamp.from(dateFormatter.parse(newEvent.start).toInstant),
      Timestamp.from(dateFormatter.parse(newEvent.end).toInstant),
      endsSameDay(newEvent.start, newEvent.end)
    )
    val eventId = calendarEventsDao.add(event).waitForResult
    calendarEventsDao.getById(eventId).waitForResult
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

  def findEventsInDateRange(userId: Long, start: Timestamp, end: Timestamp): Seq[CalendarEvent] = {
    calendarEventsDao.findEventsInDateRange(userId, start, end).waitForResult
  }

  def deleteEvent(eventId: Long): Unit = {
    calendarEventsDao.delete(eventId).waitForResult
  }

}
