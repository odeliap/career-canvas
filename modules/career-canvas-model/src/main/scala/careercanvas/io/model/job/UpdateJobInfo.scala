package careercanvas.io.model.job

import java.sql.Timestamp

case class UpdateJobInfo(
  userId: Long,
  jobId: Long,
  status: Option[JobStatus],
  appSubmissionDate: Option[Timestamp],
  notes: Option[String],
  starred: Option[Boolean]
)
