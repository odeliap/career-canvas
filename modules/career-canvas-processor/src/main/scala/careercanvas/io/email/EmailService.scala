package careercanvas.io.email

trait EmailService {

  def sendResetCode(to: String, resetCode: String): Unit

  def resetPasswordEmailText(resetCode: String): String =
    s"""
       |Enter this code to complete the reset:
       |
       |$resetCode
       |
       |If you didn't request this pin, we recommend you change your CareerCanvas password.
       |
       |Go to Profile > Edit Profile > Change password
       |
       |Thanks for helping us keep your account secure.
       |The CareerCanvas Team
       |""".stripMargin

}
