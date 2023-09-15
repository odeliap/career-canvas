package careercanvas.io.model.job

case class UserProvidedJobDetails(
  company: String,
  jobTitle: String,
  jobType: String,
  location: String,
  salaryRange: String,
  notes: Option[String]
)
