package odeliaputterman.com.jobhunter


import odeliaputterman.com.jobhunter.model.User

import scala.concurrent.Future

trait UserDao {

  def addUser(user: User): Future[Unit]

  def checkUserExists(email: String): Future[Boolean]

  def checkUserInfo(user: User): Future[Boolean]

  def updatePassword(email: String, currentPassword: String, newPassword: String): Future[Unit]

}
