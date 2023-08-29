package careercanvas.io.model.user

import play.api.mvc.QueryStringBindable

import java.sql.Timestamp
import java.time.LocalDateTime
import scala.util.Try

case class UserInfo(
  id: Long,
  email: String,
  password: String,
  fullName: String,
  lastLogin: Timestamp,
  resume: Option[String],
  linkedIn: Option[String],
  gitHub: Option[String],
  website: Option[String]
) {
  def patch(updateJobInfo: UpdateUserInfo): UserInfo = {
    this.copy(
      email = updateJobInfo.email.getOrElse(this.email),
      password = updateJobInfo.password.getOrElse(this.password),
      fullName = updateJobInfo.fullName.getOrElse(this.fullName),
      lastLogin = updateJobInfo.lastLogin.getOrElse(this.lastLogin),
      resume = updateJobInfo.resume.orElse(this.resume),
      linkedIn = updateJobInfo.linkedIn.orElse(this.linkedIn),
      gitHub = updateJobInfo.gitHub.orElse(this.gitHub),
      website = updateJobInfo.website.orElse(this.website)
    )
  }
}

object UserInfo {

  implicit object bindable extends QueryStringBindable[UserInfo] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, UserInfo]] = Option {
      Try {
        val id = params.get("id").flatMap(_.headOption).map(_.toLong)
        val email = params.get("email").flatMap(_.headOption)
        val password = params.get("password").flatMap(_.headOption)
        val fullName = params.get("fullName").flatMap(_.headOption)
        val lastLogin = params.get("lastLogin").flatMap(_.headOption).map(Timestamp.valueOf)
        val resume = params.get("resume").flatMap(_.headOption)
        val linkedIn = params.get("linkedIn").flatMap(_.headOption)
        val gitHub = params.get("gitHub").flatMap(_.headOption)
        val website = params.get("website").flatMap(_.headOption)

        // Here you can also add validation checks
        Right(UserInfo(id.getOrElse(0L), email.getOrElse(""), password.getOrElse(""), fullName.getOrElse(""), lastLogin.getOrElse(Timestamp.valueOf(LocalDateTime.now())), resume, linkedIn, gitHub, website))
      }.getOrElse(Left("Unable to bind UserInfo"))
    }

    override def unbind(key: String, userInfo: UserInfo): String = {
      val params = Seq(
        Some(s"id=${userInfo.id}"),
        Some(s"email=${userInfo.email}"),
        Some(s"password=${userInfo.password}"),
        Some(s"fullName=${userInfo.fullName}"),
        Some(s"lastLogin=${userInfo.lastLogin}"),
        userInfo.resume.map(r => s"resume=$r"),
        userInfo.linkedIn.map(li => s"linkedIn=$li"),
        userInfo.gitHub.map(gh => s"gitHub=$gh"),
        userInfo.website.map(w => s"website=$w")
      ).flatten.mkString("&")

      s"$key=$params"
    }
  }

}
