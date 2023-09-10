package careercanvas.io.dao.components

import careercanvas.io.model.security.UserResetCode
import slick.lifted.ProvenShape

import java.sql.Timestamp

trait UserResetCodesComponent {

  import careercanvas.io.dao.profile.CareerCanvasSlickProfile.api._

  val ResetCodesQuery = TableQuery[UserResetCodesTable]

  class UserResetCodesTable(tag: Tag) extends Table[UserResetCode](tag, "user_reset_codes") {

    def userId: Rep[Long] = column[Long]("user_id")
    def code: Rep[String] = column[String]("code")
    def expirationTime: Rep[Timestamp] = column[Timestamp]("expiration_time")

    def * : ProvenShape[UserResetCode] = (
      userId,
      code,
      expirationTime
    ) <> (UserResetCode.tupled, UserResetCode.unapply)
  }

}
