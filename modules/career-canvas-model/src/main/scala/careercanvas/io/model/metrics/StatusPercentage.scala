package careercanvas.io.model.metrics

import careercanvas.io.model.jobfeed.JobStatus

case class StatusPercentage(
                             userId: Long,
                             status: JobStatus,
                             percentage: Long
                           )
