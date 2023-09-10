package careercanvas.io

import careercanvas.io.model.security.UserResetCode

import java.sql.Timestamp
import scala.concurrent.Future

trait UserResetCodesDao {

  def saveCode(email: String, resetCode: String, expirationTime: Timestamp): Future[Unit]

  def getLatestCode(email: String): Future[Option[UserResetCode]]

}
