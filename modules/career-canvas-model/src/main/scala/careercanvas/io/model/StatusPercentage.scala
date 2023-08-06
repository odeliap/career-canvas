package careercanvas.io.model

case class StatusPercentage(
  userId: Long,
  status: JobStatus,
  percentage: Long
)
