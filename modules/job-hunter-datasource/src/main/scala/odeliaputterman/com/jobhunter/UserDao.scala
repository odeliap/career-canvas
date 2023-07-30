package odeliaputterman.com.jobhunter


import odeliaputterman.com.jobhunter.model.{UpdateUserInfo, User}

import scala.concurrent.Future

trait UserDao {

  def addUser(user: User): Future[Long]

  def checkUserExists(email: String): Future[Boolean]

  def getUserId(user: User): Future[Option[Long]]

  def update(updateUser: UpdateUserInfo): Future[Unit]

}
