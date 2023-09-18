package careercanvas.io.model.job

case class UpdateJobDescriptions(
  jobId: Long,
  about: Option[String],
  requirements: Option[String],
  techStack: Option[String]
)
