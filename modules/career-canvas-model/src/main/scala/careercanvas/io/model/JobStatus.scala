package careercanvas.io.model

sealed trait JobStatus extends EnumEntry

object JobStatus {
  case object NotSubmitted extends JobStatus
  case object Submitted extends JobStatus
  case object InterviewScheduled extends JobStatus
  case object Interviewed extends JobStatus
  case object OfferMade extends JobStatus
  case object Accepted extends JobStatus
  case object Rejected extends JobStatus

  private val values = Seq(NotSubmitted, Submitted, InterviewScheduled, Interviewed, OfferMade, Accepted, Rejected)

  def stringToEnum(input: String): JobStatus = {
    values
      .find(status => status.toString.equalsIgnoreCase(input))
      .getOrElse(throw new IllegalArgumentException("Input string does not match any JobStatus enum"))
  }
}