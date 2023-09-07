package careercanvas.io.dao.impl

import careercanvas.io.dao.components.JobApplicationFilesComponent
import careercanvas.io.JobApplicationFilesDao
import careercanvas.io.model.job.ApplicationFile
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import java.sql.Timestamp
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/**
 * dao to interact with the database to access application files
 * information. All the validation for calling these methods
 * should happen downstream.
 */
class JobApplicationFilesDaoImpl @Inject()(
  protected val dbConfigProvider: DatabaseConfigProvider
)(implicit ec: ExecutionContext)
  extends JobApplicationFilesDao
    with JobApplicationFilesComponent
    with HasDatabaseConfigProvider[JdbcProfile] {

  import careercanvas.io.dao.profile.CareerCanvasSlickProfile.api._

  override def add(applicationFile: ApplicationFile): Future[Long] = {
    val insertQuery = ApplicationFileQuery returning ApplicationFileQuery.map(_.fileId) += applicationFile

    db.run(insertQuery)
  }

  override def getFilesByJob(userId: Long, jobId: Long): Future[Seq[ApplicationFile]] = {
    val query = ApplicationFileQuery
      .filter(row => row.userId === userId && row.jobId === jobId)

    db.run(query.result)
  }

  override def delete(fileId: Long): Future[Unit] = {
    val deleteQuery = ApplicationFileQuery
      .filter(_.fileId === fileId)
      .delete

    db.run(deleteQuery).map(_ => ())
  }

}
