package odeliaputterman.com.jobhunter.dao.impl

import odeliaputterman.com.jobhunter.UserDao
import odeliaputterman.com.jobhunter.dao.components.UserInfoComponent
import odeliaputterman.com.jobhunter.model.{User, UserInfo}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import java.time.OffsetDateTime
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UserDaoImpl @Inject() (
  protected val dbConfigProvider: DatabaseConfigProvider
) (implicit ec: ExecutionContext)
  extends UserDao
  with UserInfoComponent
  with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  override def addUser(user: User): Future[Unit] = {
    val userInfo = UserInfo(0L, user.email, user.password, OffsetDateTime.now())

    val insert = UserInfoQuery += userInfo

    db.run(insert).map(_ => ())
  }

  override def checkUserExists(email: String): Future[Boolean] = {
    val exists = UserInfoQuery
      .filter(_.email === email).exists

    db.run(exists.result)
  }

  override def checkUserInfo(user: User): Future[Boolean]  = ???

  override def updatePassword(email: String, currentPassword: String, newPassword: String): Future[Unit] = ???

}
