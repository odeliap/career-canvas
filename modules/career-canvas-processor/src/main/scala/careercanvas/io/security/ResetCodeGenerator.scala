package careercanvas.io.security

import javax.inject.{Inject, Singleton}
import scala.util.Random

@Singleton
class ResetCodeGenerator {
  private val codeLength = 6
  private val random = new Random()

  def generateCode(): String = {
    val code = (1 to codeLength).map(_ => random.nextInt(10)).mkString
    code
  }
}
