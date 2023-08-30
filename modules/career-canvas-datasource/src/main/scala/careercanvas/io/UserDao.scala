package careercanvas.io

import careercanvas.io.model.user.{BaseUser, UpdateUserInfo, User, UserInfo}

import scala.concurrent.Future

trait UserDao {

  def addUser(user: User): Future[Long]

  def checkUserExists(email: String): Future[Boolean]

  def getUserId(user: BaseUser): Future[Option[Long]]

  def getUser(userId: Long): Future[Option[UserInfo]]

  def update(updateUser: UpdateUserInfo): Future[Unit]

}
