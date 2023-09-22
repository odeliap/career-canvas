package careercanvas.io.dao.components

import careercanvas.io.model.job.{ApplicationFile, ApplicationFileType}
import slick.lifted.ProvenShape

import java.sql.Timestamp

trait JobApplicationFilesComponent {

  import careercanvas.io.dao.profile.CareerCanvasSlickProfile.api._

  val ApplicationFileQuery = TableQuery[JobApplicationFilesTable]

  class JobApplicationFilesTable(tag: Tag) extends Table[ApplicationFile](tag, "job_application_files") {

    def userId: Rep[Long] = column[Long]("user_id")
    def jobId: Rep[Long] = column[Long]("job_id")
    def fileId: Rep[Long] = column[Long]("file_id", O.AutoInc)
    def name: Rep[String] = column[String]("name")
    def fileType: Rep[ApplicationFileType] = column[ApplicationFileType]("file_type")
    def bucket: Rep[String] = column[String]("bucket")
    def prefix: Rep[String] = column[String]("prefix")
    def lastUpdate: Rep[Timestamp] = column[Timestamp]("last_update")

    def * : ProvenShape[ApplicationFile] = (
      userId,
      jobId,
      fileId,
      name,
      fileType,
      bucket,
      prefix,
      lastUpdate
    ) <> ((ApplicationFile.apply _).tupled, ApplicationFile.unapply)
  }

}
