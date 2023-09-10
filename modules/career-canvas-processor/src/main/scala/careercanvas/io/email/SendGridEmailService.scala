package careercanvas.io.email

import com.sendgrid._
import com.sendgrid.helpers.mail.Mail
import com.sendgrid.helpers.mail.objects.{Content, Email}
import play.api.Logger

import javax.inject.Inject

class SendGridEmailService @Inject()(
  email: String,
  apiKey: String
) extends EmailService {

  val logger: Logger = Logger(getClass)

  override def sendResetEmail(to: String, resetLink: String): Unit = {
    val sg = new SendGrid(apiKey)

    val from = new Email(email)
    val subject = "Password Reset Request"
    val toEmail = new Email(to)
    val content = new Content("text/plain", s"Click the link to reset your password: $resetLink")
    val mail = new Mail(from, subject, toEmail, content)

    val request = new Request()
    try {
      request.setMethod(Method.POST)
      request.setEndpoint("mail/send")
      request.setBody(mail.build())
      val response = sg.api(request)
      logger.info(s"Reset link response status code: ${response.getStatusCode}, body: ${response.getBody}, and headers: ${response.getHeaders}")
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }

}
