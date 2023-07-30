package careercanvas.io.model

import java.sql.Timestamp
import java.time.Instant

case class JobInfo(
  jobId: Long,
  userId: Long,
  company: String,
  jobTitle: String,
  postingUrl: String,
  status: JobStatus,
  appSubmissionDate: Option[Timestamp],
  lastUpdate: Timestamp = Timestamp.from(Instant.now()),
  interviewRound: Int = 0,
  notes: Option[String] = None
) {

  def patch(updateJobInfo: UpdateJobInfo): JobInfo = {
    this.copy(
      status = updateJobInfo.status.getOrElse(this.status),
      appSubmissionDate = updateJobInfo.appSubmissionDate.orElse(this.appSubmissionDate),
      interviewRound = updateJobInfo.interviewRound.getOrElse(this.interviewRound),
      notes = updateJobInfo.notes.orElse(this.notes)
    )
  }

}