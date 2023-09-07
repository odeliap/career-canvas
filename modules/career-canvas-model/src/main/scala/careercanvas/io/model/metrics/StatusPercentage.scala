package careercanvas.io.model.metrics

import careercanvas.io.model.feed.JobStatus

case class StatusPercentage(
                             userId: Long,
                             status: JobStatus,
                             percentage: Long
                           )
