package careercanvas.io.email

import play.api.Logger

import javax.mail.{Message, Session}
import javax.mail.internet.{InternetAddress, MimeMessage}
import javax.mail.Authenticator
import javax.mail.PasswordAuthentication
import java.util.Properties

class GmailEmailService(user: String, password: String) extends EmailService {

  val logger: Logger = Logger(getClass)

  private val smtpHost = "smtp.gmail.com"
  private val smtpPort = "587"

  private val props: Properties = new Properties()
  props.put("mail.smtp.auth", "true")
  props.put("mail.smtp.starttls.enable", "true")
  props.put("mail.smtp.host", smtpHost)
  props.put("mail.smtp.port", smtpPort)

  private val session: Session = Session.getInstance(props, new Authenticator() {
    override protected def getPasswordAuthentication: PasswordAuthentication = {
      new PasswordAuthentication(user, password)
    }
  })

  override def sendResetCode(to: String, resetCode: String): Unit = {
    try {
      val message = new MimeMessage(session)
      message.setFrom(new InternetAddress(user))
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to).asInstanceOf[Array[javax.mail.Address]])
      message.setSubject("Password Reset Request")
      message.setText(resetPasswordEmailText(resetCode))

      javax.mail.Transport.send(message)
      logger.info(s"Sent reset link to $to successfully!")
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }
}