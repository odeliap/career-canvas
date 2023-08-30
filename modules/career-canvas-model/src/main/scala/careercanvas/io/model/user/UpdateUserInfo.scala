package careercanvas.io.model.user

import java.sql.Timestamp

case class UpdateUserInfo(
  id: Long,
  email: Option[String] = None,
  password: Option[String] = None,
  fullName: Option[String] = None,
  lastLogin: Option[Timestamp] = None,
  resumeId: Option[Long] = None,
  linkedIn: Option[String] = None,
  gitHub: Option[String] = None,
  website: Option[String] = None,
)
