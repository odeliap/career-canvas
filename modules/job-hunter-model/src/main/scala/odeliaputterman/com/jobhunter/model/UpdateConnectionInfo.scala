package odeliaputterman.com.jobhunter.model

import java.time.OffsetDateTime

case class UpdateConnectionInfo(
  userId: Long,
  connectionId: Long,
  company: Option[String],
  jobTitle: Option[String],
  email: Option[String],
  phoneNumber: Option[String],
  proximity: Option[ConnectionCloseness],
  lastContacted: Option[OffsetDateTime],
  notes: Option[String]
)
