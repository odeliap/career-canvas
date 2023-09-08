package careercanvas.io.model.job

import careercanvas.io.model.EnumEntry
import play.api.libs.json.{Format, JsError, JsString, JsSuccess, Reads, Writes}

sealed trait ApplicationFileType extends EnumEntry

object ApplicationFileType {

  case object CoverLetter extends ApplicationFileType
  case object Response extends ApplicationFileType

  private val values = Seq(CoverLetter, Response)

  val default: ApplicationFileType = CoverLetter

  implicit val applicationFileTypeReads: Reads[ApplicationFileType] = {
    case JsString("CoverLetter") => JsSuccess(CoverLetter)
    case JsString("Response")    => JsSuccess(Response)
    case _ => JsError("Cannot parse as ApplicationFileType")
  }

  implicit val applicationFileTypeWrites: Writes[ApplicationFileType] = {
    case CoverLetter => JsString("CoverLetter")
    case Response    => JsString("Response")
  }

  implicit val applicationFileTypeFormat: Format[ApplicationFileType] = Format(applicationFileTypeReads, applicationFileTypeWrites)

  def stringToEnum(input: String): ApplicationFileType = {
    values
      .find(status => status.toString.equalsIgnoreCase(input))
      .getOrElse(throw new IllegalArgumentException("Input string does not match any ApplicationFile enum"))
  }
}
