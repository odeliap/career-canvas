package odeliaputterman.com.jobhunter.model

import java.time.OffsetDateTime

case class UserInfo(
  id: Long,
  email: String,
  password: String,
  lastLogin: OffsetDateTime
)
