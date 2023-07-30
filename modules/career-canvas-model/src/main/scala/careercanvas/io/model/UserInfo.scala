package careercanvas.io.model

import java.sql.Timestamp

case class UserInfo(
  id: Long,
  email: String,
  password: String,
  lastLogin: Timestamp
) {
  def patch(updateJobInfo: UpdateUserInfo): UserInfo = {
    this.copy(
      email = updateJobInfo.email.getOrElse(this.email),
      password = updateJobInfo.password.getOrElse(this.password),
      lastLogin = updateJobInfo.lastLogin.getOrElse(this.lastLogin)
    )
  }
}