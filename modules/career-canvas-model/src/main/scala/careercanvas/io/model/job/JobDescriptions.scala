package careercanvas.io.model.job

case class JobDescriptions(
  jobId: Long,
  about: String,
  requirements: String,
  techStack: String
) extends CompletionResolvedInfo