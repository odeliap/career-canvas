package careercanvas.io.model.job

import careercanvas.io.model.EnumEntry
import play.api.libs.json.{JsString, Reads, Writes}

sealed trait JobType extends EnumEntry

object JobType {
  case object FullTime extends JobType
  case object Contract extends JobType
  case object PartTime extends JobType
  case object Internship extends JobType
  case object Temporary extends JobType
  case object Unknown extends JobType

  val default: JobType = FullTime

  val values = Seq(FullTime, Contract, PartTime, Internship, Temporary, Unknown)

  implicit val jobTypeReads: Reads[JobType] = Reads { json =>
    json.validate[String].map(stringToEnum)
  }

  implicit val jobTypeWrites: Writes[JobType] = Writes { jobStatus =>
    JsString(jobStatus.toString)
  }

  def stringToEnum(input: String): JobType = {
    values
      .find(status => status.toString.equalsIgnoreCase(input))
      .getOrElse(throw new IllegalArgumentException("Input string does not match any JobType enum"))
  }

}