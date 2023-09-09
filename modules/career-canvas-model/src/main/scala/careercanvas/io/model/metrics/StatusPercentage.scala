package careercanvas.io.model.metrics

import careercanvas.io.model.job.JobStatus

case class StatusPercentage(
  status: JobStatus,
  percentage: Double
)
