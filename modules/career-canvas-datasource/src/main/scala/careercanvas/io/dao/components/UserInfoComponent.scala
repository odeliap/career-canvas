package careercanvas.io.dao.components

import careercanvas.io.model.user.UserInfo
import slick.lifted.ProvenShape

import java.sql.Timestamp

trait UserInfoComponent {

  import careercanvas.io.dao.profile.CareerCanvasSlickProfile.api._

  val UserInfoQuery = TableQuery[UserInfoTable]

  class UserInfoTable(tag: Tag) extends Table[UserInfo](tag, "user_info") {

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def email: Rep[String] = column[String]("email")
    def password: Rep[String] = column[String]("password")
    def fullName: Rep[String] = column[String]("full_name")
    def lastLogin: Rep[Timestamp] = column[Timestamp]("last_login")
    def resumeId: Rep[Option[Long]] = column[Option[Long]]("resume_id")
    def linkedIn: Rep[Option[String]] = column[Option[String]]("linkedin")
    def gitHub: Rep[Option[String]] = column[Option[String]]("github")
    def website: Rep[Option[String]] = column[Option[String]]("website")

    def * : ProvenShape[UserInfo] = (
      id,
      email,
      password,
      fullName,
      lastLogin,
      resumeId,
      linkedIn,
      gitHub,
      website
    ) <> ((UserInfo.apply _).tupled, UserInfo.unapply)

  }

}
