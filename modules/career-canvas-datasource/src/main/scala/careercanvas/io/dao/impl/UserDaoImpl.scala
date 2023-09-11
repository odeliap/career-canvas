package careercanvas.io.dao.impl

import careercanvas.io.UserDao
import careercanvas.io.converter.Converters
import careercanvas.io.dao.components.UserInfoComponent
import careercanvas.io.model.user._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import java.time.OffsetDateTime
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/**
 * dao to interact with the database to access user
 * information. All the validation for calling these methods
 * should happen downstream.
 */
class UserDaoImpl @Inject() (
  protected val dbConfigProvider: DatabaseConfigProvider
) (implicit ec: ExecutionContext)
  extends UserDao
  with UserInfoComponent
  with HasDatabaseConfigProvider[JdbcProfile]
  with Converters {

  import profile.api._

  override def addUser(user: User): Future[Long] = {
    val newUserInfo = UserInfo(
      0L,
      user.email,
      user.password,
      user.fullName,
      OffsetDateTime.now(),
      None,
      None,
      None
    )

  val insertQuery = UserInfoQuery returning UserInfoQuery.map(_.id) += newUserInfo

    db.run(insertQuery)
  }

  override def checkUserExists(email: String): Future[Boolean] = {
    val userExistsQuery = UserInfoQuery
      .filter(_.email === email).exists

    db.run(userExistsQuery.result)
  }

  override def getUser(userId: Long): Future[Option[UserInfo]] = {
    val userQuery = UserInfoQuery
      .filter(_.id === userId)

    db.run(userQuery.result.headOption)
  }

  override def getUserId(user: BaseUser): Future[Option[Long]]  = {
    val userIdQuery = UserInfoQuery
      .filter(row => row.email === user.email && row.password === user.password)
      .map(_.id)

    db.run(userIdQuery.result.headOption)
  }

  override def getUserIdFromEmail(email: String): Future[Option[Long]] = {
    val userIdQuery = UserInfoQuery
      .filter(_.email === email)
      .map(_.id)

    db.run(userIdQuery.result.headOption)
  }

  override def update(updateUser: UpdateUserInfo): Future[Unit] = {
    val userInfoQuery = UserInfoQuery
      .filter(_.id ===updateUser.id)

    val tx = for {
      userInfo <- userInfoQuery.result.head

      _ <- userInfoQuery.update(userInfo.patch(updateUser))
    } yield ()

    db.run(tx.transactionally)
  }

}
