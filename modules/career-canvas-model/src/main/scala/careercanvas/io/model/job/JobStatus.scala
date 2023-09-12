package careercanvas.io.model.job

import careercanvas.io.model.EnumEntry
import play.api.libs.json.{JsString, Reads, Writes}

sealed trait JobStatus extends EnumEntry

object JobStatus {
  case object NotSubmitted extends JobStatus
  case object Submitted extends JobStatus
  case object InterviewScheduled extends JobStatus
  case object Interviewed extends JobStatus
  case object OfferMade extends JobStatus
  case object Rejected extends JobStatus

  private val values = Seq(NotSubmitted, Submitted, InterviewScheduled, Interviewed, OfferMade, Rejected)

  implicit val jobStatusReads: Reads[JobStatus] = Reads { json =>
    json.validate[String].map(stringToEnum)
  }

  implicit val jobStatusWrites: Writes[JobStatus] = Writes { jobStatus =>
    JsString(jobStatus.toString)
  }

  def stringToEnum(input: String): JobStatus = {
    values
      .find(status => status.toString.equalsIgnoreCase(input))
      .getOrElse(throw new IllegalArgumentException("Input string does not match any JobStatus enum"))
  }

}