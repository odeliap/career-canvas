package odeliaputterman.com.jobhunter.dao.components

import odeliaputterman.com.jobhunter.model.UserInfo
import slick.lifted.ProvenShape

import java.sql.Timestamp

trait UserInfoComponent {

  import odeliaputterman.com.jobhunter.dao.profile.JobHunterSlickProfile.api._

  val UserInfoQuery = TableQuery[UserInfoTable]

  class UserInfoTable(tag: Tag) extends Table[UserInfo](tag, "user_info") {

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def email: Rep[String] = column[String]("email")
    def password: Rep[String] = column[String]("password")
    def lastLogin: Rep[Timestamp] = column[Timestamp]("last_login")

    def * : ProvenShape[UserInfo] = (
      id,
      email,
      password,
      lastLogin
    ) <> (UserInfo.tupled, UserInfo.unapply)

  }

}
