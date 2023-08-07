package controllers

import authentication.AuthenticatedUserMessagesAction
import model.{Global, NewEvent}
import service.CalendarEventsService
import play.api.mvc._
import play.api.libs.json.{JsResult, JsSuccess, JsValue, Json}
import play.twirl.api.Html

import javax.inject._

@Singleton
class CalendarController @Inject()(
  cc: MessagesControllerComponents,
  authenticatedUserMessagesAction: AuthenticatedUserMessagesAction,
  calendarEventsService: CalendarEventsService
) extends MessagesAbstractController(cc) {

  private val logger = play.api.Logger(this.getClass)

  def showHomeCalendar(): Action[AnyContent] = authenticatedUserMessagesAction { request: MessagesRequest[AnyContent] =>
    val userId = request.session.data(Global.SESSION_USER_ID)
    val events = calendarEventsService
      .findAllEvents(userId)
      .map(event => (event.eventId, event.title, event.start, event.end))
    val eventsHtml = new Html(Json.toJson(events.toList).toString())
    Ok(views.html.calendar.calendar(eventsHtml))
  }

  def addByAjax(): Action[AnyContent] = authenticatedUserMessagesAction { request: MessagesRequest[AnyContent] =>
    val json = request.body.asJson.get
    val event = json.as[NewEvent]
    val userId = request.session.data(Global.SESSION_USER_ID)
    val addedEvent = calendarEventsService.addEvent(userId, event)
    val addedEventMap = addedEvent.getClass.getDeclaredFields.foldLeft(Map.empty[String, String]) { (a, f) =>
      f.setAccessible(true)
      a + (f.getName -> f.get(addedEvent).toString)
    }
    Ok(Json.toJson(addedEventMap))
  }

}
