package authentication

import play.api.mvc.Results._
import play.api.mvc._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AuthenticatedUserAction @Inject() (parser: BodyParsers.Default)(implicit ec: ExecutionContext)
  extends ActionBuilderImpl(parser) {

  private val logger = play.api.Logger(this.getClass)

  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
    logger.info("ENTERED AuthenticatedUserAction::invokeBlock ...")
    val maybeUsername = request.session.get(model.Global.SESSION_USERNAME_KEY)
    maybeUsername match {
      case None =>
        Future.successful(Forbidden("" +
          "You’re not logged in. Please login to see this page."))
      case Some(u) =>
        val res: Future[Result] = block(request)
        res
    }
  }
}