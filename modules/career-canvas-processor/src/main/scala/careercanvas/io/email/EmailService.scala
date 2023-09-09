package careercanvas.io.email

trait EmailService {

  def sendResetEmail(to: String, resetLink: String): Unit

}
