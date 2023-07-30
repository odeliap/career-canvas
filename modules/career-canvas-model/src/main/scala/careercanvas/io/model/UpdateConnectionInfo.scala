package careercanvas.io.model

import java.sql.Timestamp

case class UpdateConnectionInfo(
  connectionId: Long,
  userId: Long,
  company: Option[String],
  jobTitle: Option[String],
  email: Option[String],
  phoneNumber: Option[String],
  proximity: Option[ConnectionCloseness],
  lastContacted: Option[Timestamp],
  notes: Option[String]
)
