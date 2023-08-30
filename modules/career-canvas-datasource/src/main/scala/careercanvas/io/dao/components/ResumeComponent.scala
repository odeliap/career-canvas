package careercanvas.io.dao.components

import careercanvas.io.model.user.Resume
import slick.lifted.ProvenShape

import java.sql.Timestamp

trait ResumeComponent {

  import careercanvas.io.dao.profile.CareerCanvasSlickProfile.api._

  val ResumeQuery = TableQuery[ResumesTable]

  class ResumesTable(tag: Tag) extends Table[Resume](tag, "resumes") {

    def userId: Rep[Long] = column[Long]("user_id")
    def resumeId: Rep[Long] = column[Long]("resume_id", O.AutoInc)
    def name: Rep[String] = column[String]("name")
    def locationPath: Rep[String] = column[String]("location_path")
    def uploadDate: Rep[Timestamp] = column[Timestamp]("upload_date")

    def * : ProvenShape[Resume] = (
      userId,
      resumeId,
      name,
      locationPath,
      uploadDate
    ) <> (Resume.tupled, Resume.unapply)

  }

}
