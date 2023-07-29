package odeliaputterman.com.jobhunter.model

case class UpdateJobInfo(
  userId: Long,
  jobId: Long,
  status: Option[JobStatus],
  interviewRound: Option[Int],
  notes: Option[String]
)
