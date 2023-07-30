package service

import odeliaputterman.com.jobhunter.UserDao
import odeliaputterman.com.jobhunter.converter.Converters
import odeliaputterman.com.jobhunter.model.{UpdateUserInfo, User}
import odeliaputterman.com.jobhunter.util.AwaitResult

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
      case true => Future.successful(None) // User already exists, so creation is not attempted.
      case false =>
        userDao.addUser(user).map(Option(_)) // User does not exist, so attempt to create user.
    }.waitForResult
  }

  def updateUserPassword(id: Long, newPassword: String): Unit = {
    userDao.update(UpdateUserInfo(id = id, password = Option(newPassword))).waitForResult
  }

}
