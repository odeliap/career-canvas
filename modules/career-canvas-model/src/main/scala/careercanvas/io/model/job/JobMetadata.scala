package careercanvas.io.model.job

case class JobMetadata(
  jobId: Long,
  location: String,
  salary: String,
  jobDescription: String,
  companyDescription: String
)