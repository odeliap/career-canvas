package service

import careercanvas.io.UserDao
import careercanvas.io.converter.Converters
import careercanvas.io.model.user.{BaseUser, UpdateUserInfo, User}
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

  def lookupUser(user: BaseUser): Option[Long] = {
    userDao.getUserId(user).flatMap { userIdOption =>
      userIdOption.map { userId =>
        userDao.getUser(userId)
        userDao.update(UpdateUserInfo(userId, lastLogin = Option(OffsetDateTime.now()))).map(_ => Some(userId))
      }.getOrElse(Future.successful(None))
    }.waitForResult
  }

  def getUserName(userId: Long): Option[String] = {
    userDao.getUser(userId).map {
      case Some(info) => Some(info.fullName)
      case None => None
    }.waitForResult
  }

  def attemptUserCreation(user: User): Option[Long] = {
    userDao.checkUserExists(user.email).flatMap {
      case true => Future.successful(None)
      case false =>
        userDao.addUser(user).map(Option(_))
    }.waitForResult
  }

  def updateUserPassword(id: String, newPassword: String): Unit = {
    userDao.update(UpdateUserInfo(id = id.toLong, password = Option(newPassword))).waitForResult
  }

  def checkValidEmail(email: Option[String]): Boolean = {
    if (email.isDefined) {
      !userDao.checkUserExists(email.get).waitForResult
    } else {
      true
    }
  }

  def checkValidName(name: Option[String]): Boolean = {
    if (name.isDefined) {
      name.get.split("\\s+").count(_.nonEmpty) >= 2
    } else {
      true
    }
  }

}
