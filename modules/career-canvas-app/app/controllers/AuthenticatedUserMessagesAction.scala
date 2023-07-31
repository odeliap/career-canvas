package controllers

import play.api.i18n.MessagesApi

import javax.inject.Inject
import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class AuthenticatedUserMessagesAction @Inject()(bodyParser: BodyParsers.Default, messagesApi: MessagesApi) (implicit ec: ExecutionContext)
  extends ActionBuilder[MessagesRequest, AnyContent] {

  private val logger = play.api.Logger(this.getClass)

  override def parser: BodyParser[AnyContent] = bodyParser
  override protected def executionContext: ExecutionContext = ec

  override def invokeBlock[A](request: Request[A], block: MessagesRequest[A] => Future[Result]): Future[Result] = {
    logger.info("ENTERED AuthenticatedUserAction::invokeBlock ...")
    val maybeUsername = request.session.get(model.Global.SESSION_USER_ID)
    maybeUsername match {
      case None =>
        Future.successful(Forbidden("" +
          "You’re not logged in. Please login to see this page."))
      case Some(u) =>
        val res: Future[Result] = block(new MessagesRequest[A](request, messagesApi))
        res
    }
  }

}