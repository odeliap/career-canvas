package careercanvas.io.dao.impl

import careercanvas.io.JobMetadataDao
import careercanvas.io.dao.components.JobMetadataComponent
import careercanvas.io.model.job.JobMetadata
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/**
 * dao to interact with the database to access job metadata
 * information. All the validation for calling these methods
 * should happen downstream.
 */
class JobMetadataDaoImpl @Inject() (
  protected val dbConfigProvider: DatabaseConfigProvider
) (implicit ec: ExecutionContext)
  extends JobMetadataDao
    with JobMetadataComponent
    with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  override def addJob(jobMetadata: JobMetadata): Future[Unit] = {
    val insertQuery = JobMetadataQuery += jobMetadata

    db.run(insertQuery).map(_ => ())
  }

  override def removeJob(jobId: Long): Future[Unit] = {
    val removeJobQuery = JobMetadataQuery
      .filter(_.jobId === jobId)
      .delete

    db.run(removeJobQuery).map(_ => ())
  }

  override def getJobById(jobId: Long): Future[JobMetadata] = {
    val query = JobMetadataQuery
      .filter(_.jobId === jobId)

    db.run(query.result.head)
  }

}
