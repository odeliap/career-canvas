package odeliaputterman.com.jobhunter.model

import java.time.OffsetDateTime

case class JobInfo(
  userId: Long,
  jobId: Long,
  company: String,
  jobTitle: String,
  postingUrl: String,
  status: JobStatus,
  lastUpdate: OffsetDateTime = OffsetDateTime.now(),
  interviewRound: Int = 0,
  notes: Option[String] = None
) {

  def update(updateJobInfo: UpdateJobInfo): JobInfo = {
    this.copy(
      status = updateJobInfo.status.getOrElse(this.status),
      interviewRound = updateJobInfo.interviewRound.getOrElse(this.interviewRound),
      notes = updateJobInfo.notes.orElse(this.notes)
    )
  }

}