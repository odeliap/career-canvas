package odeliaputterman.com.jobhunter.model

import java.sql.Timestamp

case class UpdateUserInfo(
  id: Long,
  email: Option[String] = None,
  password: Option[String] = None,
  lastLogin: Option[Timestamp] = None
)