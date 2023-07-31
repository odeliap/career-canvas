package careercanvas.io.model

import java.sql.Timestamp
import java.time.Instant

case class JobInfo(
  userId: Long,
  jobId: Long,
  company: String,
  jobTitle: String,
  postUrl: String,
  status: JobStatus,
  appSubmissionDate: Option[Timestamp],
  lastUpdate: Timestamp = Timestamp.from(Instant.now()),
  interviewRound: Option[Int] = Option(0),
  notes: Option[String] = None
) {

  def patch(updateJobInfo: UpdateJobInfo): JobInfo = {
    this.copy(
      status = updateJobInfo.status.getOrElse(this.status),
      appSubmissionDate = updateJobInfo.appSubmissionDate.orElse(this.appSubmissionDate),
      interviewRound = updateJobInfo.interviewRound.orElse(this.interviewRound),
      notes = updateJobInfo.notes.orElse(this.notes)
    )
  }

}