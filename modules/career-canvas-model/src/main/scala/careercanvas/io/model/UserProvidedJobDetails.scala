package careercanvas.io.model

import java.sql.Timestamp

case class UserProvidedJobDetails(
  company: String,
  jobTitle: String,
  status: String,
  interviewRound: Option[Int],
  appSubmissionDate: Option[Timestamp],
  notes: Option[String]
)
