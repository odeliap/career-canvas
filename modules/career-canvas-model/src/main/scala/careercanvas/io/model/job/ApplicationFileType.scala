package careercanvas.io.model.job

import careercanvas.io.model.EnumEntry

sealed trait ApplicationFileType extends EnumEntry

object ApplicationFileType {
  case object CoverLetter extends ApplicationFileType
  case object Response extends ApplicationFileType

  private val values = Seq(CoverLetter, Response)

  def stringToEnum(input: String): ApplicationFileType = {
    values
      .find(status => status.toString.equalsIgnoreCase(input))
      .getOrElse(throw new IllegalArgumentException("Input string does not match any ApplicationFile enum"))
  }
}