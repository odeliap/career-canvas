package careercanvas.io.model.job

case class UserProvidedJobDetails(
  company: String,
  jobTitle: String,
  jobType: String,
  location: String,
  salaryRange: String,
  status: String,
  interviewRound: Option[Int],
  notes: Option[String]
)
