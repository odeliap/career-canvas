package careercanvas.io.model.security

import java.sql.Timestamp

case class UserResetCode(
  userId: Long,
  code: String,
  expirationTime: Timestamp
)
