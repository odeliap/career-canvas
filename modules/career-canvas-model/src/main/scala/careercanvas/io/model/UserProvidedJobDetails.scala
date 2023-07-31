package careercanvas.io.model

case class UserProvidedJobDetails(
  company: String,
  jobTitle: String,
  status: String,
  interviewRound: Option[Int],
  notes: Option[String]
)
