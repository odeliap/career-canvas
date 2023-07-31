package service

import careercanvas.io.UserDao
import careercanvas.io.converter.Converters
import careercanvas.io.model.{UpdateUserInfo, User}
import careercanvas.io.util.AwaitResult

import java.time.OffsetDateTime
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

@javax.inject.Singleton
class UserService @Inject()(
  userDao: UserDao
)(implicit ec: ExecutionContext)
  extends AwaitResult
  with Converters {

  def lookupUser(user: User): Option[Long] = {
    userDao.getUserId(user).flatMap { userIdOption =>
      userIdOption.map { userId =>
        userDao.update(UpdateUserInfo(userId, lastLogin = Option(OffsetDateTime.now()))).map(_ => Some(userId))
      }.getOrElse(Future.successful(None))
    }.waitForResult
  }

  def attemptUserCreation(user: User): Option[Long] = {
    userDao.checkUserExists(user.email).flatMap {
      case true => Future.successful(None)
      case false =>
        userDao.addUser(user).map(Option(_))
    }.waitForResult
  }

  def updateUserPassword(id: Long, newPassword: String): Unit = {
    userDao.update(UpdateUserInfo(id = id, password = Option(newPassword))).waitForResult
  }

}
