package careercanvas.io.dao.components

import careercanvas.io.model.{ConnectionCloseness, ConnectionInfo}
import slick.lifted.ProvenShape

import java.sql.Timestamp

trait NetworkComponent {

  import careercanvas.io.dao.profile.CareerCanvasSlickProfile.api._

  val NetworkQuery = TableQuery[NetworkTable]

  class NetworkTable(tag: Tag) extends Table[ConnectionInfo](tag, "connections") {

    def userId: Rep[Long] = column[Long]("user_id")
    def connectionId: Rep[Long] = column[Long]("connection_id", O.AutoInc)
    def firstName: Rep[String] = column[String]("first_name")
    def lastName: Rep[String] = column[String]("last_name")
    def company: Rep[String] = column[String]("company")
    def jobTitle: Rep[String] = column[String]("job_title")
    def email: Rep[String] = column[String]("email")
    def phoneNumber: Rep[Option[String]] = column[Option[String]]("phone_number")
    def proximity: Rep[ConnectionCloseness] = column[ConnectionCloseness]("proximity")
    def lastContacted: Rep[Option[Timestamp]] = column[Option[Timestamp]]("last_contacted")
    def notes: Rep[Option[String]] = column[Option[String]]("notes")

    def * : ProvenShape[ConnectionInfo] = (
      userId,
      connectionId,
      firstName,
      lastName,
      company,
      jobTitle,
      email,
      phoneNumber,
      proximity,
      lastContacted,
      notes
    ) <> (ConnectionInfo.tupled, ConnectionInfo.unapply)

  }

}
