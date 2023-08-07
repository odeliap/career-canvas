package service

import careercanvas.io.CalendarEventsDao
import careercanvas.io.model.{CalendarEvent, UpdateCalendarEvent}
import careercanvas.io.util.AwaitResult

import java.sql.Timestamp
import java.text.SimpleDateFormat
import javax.inject.Inject
import scala.concurrent.ExecutionContext

class CalendarEventsService @Inject() (
  calendarEventsDao: CalendarEventsDao
)(implicit ec: ExecutionContext)
  extends AwaitResult {

  def endsSameDay(start: Timestamp, end: Timestamp): Boolean = {
    val dateFormat = new SimpleDateFormat("yyyyMMdd")
    dateFormat.format(start).equals(dateFormat.format(end))
  }

  def addEvent(calendarEvent: CalendarEvent): Long = {
    calendarEventsDao.add(calendarEvent).waitForResult
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
