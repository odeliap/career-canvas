package careercanvas.io.model

import java.sql.Timestamp

case class UserProvidedJobDetails(
  status: String,
  interviewRound: Option[Int],
  appSubmissionDate: Option[Timestamp],
  notes: Option[String]
)
