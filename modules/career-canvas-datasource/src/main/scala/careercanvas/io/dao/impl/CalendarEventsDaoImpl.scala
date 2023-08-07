package careercanvas.io.dao.impl

import careercanvas.io.CalendarEventsDao
import careercanvas.io.dao.components.CalendarEventsComponent
import careercanvas.io.model.{CalendarEvent, UpdateCalendarEvent}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import java.sql.Timestamp
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/**
 * dao to interact with the database to access calendar events
 * information. All the validation for calling these methods
 * should happen downstream.
 */
class CalendarEventsDaoImpl @Inject() (
  protected val dbConfigProvider: DatabaseConfigProvider
) (implicit ec: ExecutionContext)
  extends CalendarEventsDao
  with CalendarEventsComponent
  with HasDatabaseConfigProvider[JdbcProfile] {

  import careercanvas.io.dao.profile.CareerCanvasSlickProfile.api._

  override def add(event: CalendarEvent): Future[CalendarEvent] = {
    val insertQuery = CalendarEventsQuery returning CalendarEventsQuery += event

    db.run(insertQuery)
  }

  override def edit(updateCalendarEvent: UpdateCalendarEvent): Future[Unit] = {
    val eventInfoQuery = CalendarEventsQuery
      .filter(_.eventId === updateCalendarEvent.eventId)

    val tx = for {
      eventInfo <- eventInfoQuery.result.head

      _ <- eventInfoQuery.update(eventInfo.patch(updateCalendarEvent))
    } yield ()

    db.run(tx.transactionally)
  }

  override def getById(eventId: Long): Future[CalendarEvent] = {
    val eventQuery = CalendarEventsQuery
      .filter(_.eventId === eventId)

    db.run(eventQuery.result.head)
  }

  override def findAllEvents(userId: Long): Future[Seq[CalendarEvent]] = {
    val eventsQuery = CalendarEventsQuery
      .filter(_.userId === userId)
      .sortBy(_.start.desc)

    db.run(eventsQuery.result)
  }

  override def findEventsInDateRange(userId: Long, start: Timestamp, end: Timestamp): Future[Seq[CalendarEvent]] = {
    val eventsQuery = CalendarEventsQuery
      .filter(event => event.userId === userId && event.start >= start && event.end <= end)

    db.run(eventsQuery.result)
  }

  override def delete(eventId: Long): Future[Unit] = {
    val deleteQuery = CalendarEventsQuery
      .filter(_.eventId === eventId)
      .delete

    db.run(deleteQuery).map(_ => ())
  }

}
