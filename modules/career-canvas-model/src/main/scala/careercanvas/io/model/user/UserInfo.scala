package careercanvas.io.model.user

import java.sql.Timestamp

case class UserInfo(
                     id: Long,
                     email: String,
                     password: String,
                     fullName: String,
                     lastLogin: Timestamp
                   ) {
  def patch(updateJobInfo: UpdateUserInfo): UserInfo = {
    this.copy(
      email = updateJobInfo.email.getOrElse(this.email),
      password = updateJobInfo.password.getOrElse(this.password),
      fullName = updateJobInfo.fullName.getOrElse(this.fullName),
      lastLogin = updateJobInfo.lastLogin.getOrElse(this.lastLogin)
    )
  }
}
