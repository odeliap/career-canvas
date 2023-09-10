package service

import careercanvas.io.{UserDao, UserResetCodesDao}
import careercanvas.io.converter.Converters
import careercanvas.io.email.EmailService
import careercanvas.io.model.user.UpdateUserInfo
import careercanvas.io.security.ResetCodeGenerator
import careercanvas.io.util.AwaitResult

import java.sql.Timestamp
import java.time.LocalDateTime
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class ForgotPasswordService @Inject()(
  userDao: UserDao,
  userResetCodesDao: UserResetCodesDao,
  emailService: EmailService,
  resetCodeGenerator: ResetCodeGenerator
)(implicit ec: ExecutionContext)
  extends AwaitResult
  with Converters {

  def updateUserPassword(id: String, newPassword: String): Unit = {
    userDao.update(UpdateUserInfo(id = id.toLong, password = Option(newPassword))).waitForResult
  }

  def verifyResetCode(email: String, code: String): Boolean = {
    val currentTime = LocalDateTime.now()

    userResetCodesDao.getLatestCode(email).waitForResult match {
      case Some(latestCode) if latestCode.expirationTime.toLocalDateTime.isAfter(currentTime) =>
        code == latestCode.code
      case _ =>
        false
    }
  }

  def sendResetPasswordLink(email: String): Unit = {
    val resetCode = resetCodeGenerator.generateCode()
    val expirationTime = Timestamp.valueOf(LocalDateTime.now().plusMinutes(15))
    userResetCodesDao.saveCode(email, resetCode, expirationTime)
    emailService.sendResetCode(email, resetCode)
  }


}
