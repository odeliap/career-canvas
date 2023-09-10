package careercanvas.io.dao.impl

import careercanvas.io.UserResetCodesDao
import careercanvas.io.dao.components.{UserInfoComponent, UserResetCodesComponent}
import careercanvas.io.model.security.UserResetCode
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import java.sql.Timestamp
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/**
 * dao to interact with the database to access user reset codes
 * information. All the validation for calling these methods
 * should happen downstream.
 */
class UserResetCodesDaoImpl @Inject() (
  protected val dbConfigProvider: DatabaseConfigProvider
)(implicit ec: ExecutionContext)
  extends UserResetCodesDao
  with UserResetCodesComponent
  with UserInfoComponent
  with HasDatabaseConfigProvider[JdbcProfile] {

  import careercanvas.io.dao.profile.CareerCanvasSlickProfile.api._

  override def saveCode(email: String, resetCode: String, expirationTime: Timestamp): Future[Unit] = {
    val tx = for {
      userId <- UserInfoQuery
        .filter(_.email === email)
        .map(_.id)
        .result
        .head

      _ <- ResetCodesQuery += UserResetCode(userId, resetCode, expirationTime)
    } yield ()

    db.run(tx.transactionally)
  }

  override def getLatestCode(email: String): Future[Option[UserResetCode]] = {
    val tx = for {
      userId <- UserInfoQuery
        .filter(_.email === email)
        .map(_.id)
        .result
        .head

      latestCode <- ResetCodesQuery
        .filter(_.userId === userId)
        .sortBy(_.expirationTime.desc)
        .result
        .headOption
    } yield (latestCode)

    db.run(tx.transactionally)
  }

}
