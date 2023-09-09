package careercanvas.io.model.job

case class JobsResult(
  jobs: Seq[JobInfo],
  hasNext: Boolean
)
