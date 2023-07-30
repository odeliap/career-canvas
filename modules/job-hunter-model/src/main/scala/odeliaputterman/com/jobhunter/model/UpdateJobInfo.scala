package odeliaputterman.com.jobhunter.model

import java.sql.Timestamp

case class UpdateJobInfo(
  userId: Long,
  jobId: Long,
  status: Option[JobStatus],
  appSubmissionDate: Option[Timestamp],
  interviewRound: Option[Int],
  notes: Option[String]
)
