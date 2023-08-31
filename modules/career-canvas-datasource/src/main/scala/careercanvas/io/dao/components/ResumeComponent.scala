package careercanvas.io.dao.components

import careercanvas.io.model.user.Resume
import slick.lifted.ProvenShape

import java.sql.Timestamp

trait ResumeComponent {

  import careercanvas.io.dao.profile.CareerCanvasSlickProfile.api._

  val ResumeQuery = TableQuery[ResumesTable]

  class ResumesTable(tag: Tag) extends Table[Resume](tag, "resumes") {

    def userId: Rep[Long] = column[Long]("user_id")
    def version: Rep[Int] = column[Int]("version")
    def name: Rep[String] = column[String]("name")
    def bucket: Rep[String] = column[String]("bucket")
    def prefix: Rep[String] = column[String]("prefix")
    def uploadDate: Rep[Timestamp] = column[Timestamp]("upload_date")

    def * : ProvenShape[Resume] = (
      userId,
      version,
      name,
      bucket,
      prefix,
      uploadDate
    ) <> (Resume.tupled, Resume.unapply)

  }

}
