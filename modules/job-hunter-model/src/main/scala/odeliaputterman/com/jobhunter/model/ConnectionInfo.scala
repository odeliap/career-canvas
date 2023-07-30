package odeliaputterman.com.jobhunter.model

import java.sql.Timestamp

case class ConnectionInfo(
  userId: Long,
  connectionId: Long,
  firstName: String,
  lastName: String,
  company: String,
  jobTitle: String,
  email: String,
  phoneNumber: Option[String],
  proximity: ConnectionCloseness,
  lastContacted: Option[Timestamp] = None,
  notes: Option[String] = None
) {

  def patch(updateConnectionInfo: UpdateConnectionInfo): ConnectionInfo = {
    this.copy(
      company = updateConnectionInfo.company.getOrElse(this.company),
      jobTitle = updateConnectionInfo.jobTitle.getOrElse(this.jobTitle),
      email = updateConnectionInfo.email.getOrElse(this.email),
      phoneNumber = updateConnectionInfo.phoneNumber.orElse(this.phoneNumber),
      proximity = updateConnectionInfo.proximity.getOrElse(this.proximity),
      lastContacted = updateConnectionInfo.lastContacted.orElse(this.lastContacted),
      notes = updateConnectionInfo.notes.orElse(this.notes)
    )
  }

}
