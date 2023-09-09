package careercanvas.io.email

import com.sendgrid._
import com.sendgrid.helpers.mail.Mail
import com.sendgrid.helpers.mail.objects.{Content, Email}

import javax.inject.Inject

class SendGridEmailService @Inject()(
  email: String,
  apiKey: String
) extends EmailService {

  override def sendResetEmail(to: String, resetLink: String): Unit = {
    val sg = new SendGrid(apiKey)

    val from = new Email(email)   // Replace with your email or alias
    val subject = "Reset Your Password"
    val toEmail = new Email(to)
    val content = new Content("text/plain", s"Click on this link to reset your password: $resetLink")
    val mail = new Mail(from, subject, toEmail, content)

    val request = new Request()
    try {
      request.setMethod(Method.POST)
      request.setEndpoint("mail/send")
      request.setBody(mail.build())
      val response = sg.api(request)
      println(response.getStatusCode)
      println(response.getBody)
      println(response.getHeaders)
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }

}
