package careercanvas.io.model.metrics

import careercanvas.io.model.job.JobStatus

case class StatusPercentage(
                             userId: Long,
                             status: JobStatus,
                             percentage: Long
                           )
