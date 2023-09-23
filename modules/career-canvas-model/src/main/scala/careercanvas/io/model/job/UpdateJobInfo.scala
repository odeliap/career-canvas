package careercanvas.io.model.job

import java.sql.Timestamp
import java.time.LocalDateTime

case class UpdateJobInfo(
  userId: Long,
  jobId: Long,
  status: Option[JobStatus],
  jobType: Option[JobType],
  appSubmissionDate: Option[Timestamp],
  dateAdded: Option[Timestamp],
  starred: Option[Boolean],
  lastUpdate: Timestamp = Timestamp.valueOf(LocalDateTime.now())
)
