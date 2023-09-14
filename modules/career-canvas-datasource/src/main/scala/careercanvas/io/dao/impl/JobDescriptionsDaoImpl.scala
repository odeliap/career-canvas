package careercanvas.io.dao.impl

import careercanvas.io.JobDescriptionsDao
import careercanvas.io.dao.components.JobDescriptionsComponent
import careercanvas.io.model.job.JobDescriptions
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/**
 * dao to interact with the database to access job metadata
 * information. All the validation for calling these methods
 * should happen downstream.
 */
class JobDescriptionsDaoImpl @Inject()(
  protected val dbConfigProvider: DatabaseConfigProvider
)(implicit ec: ExecutionContext)
  extends JobDescriptionsDao
    with JobDescriptionsComponent
    with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  override def addJob(jobDescriptions: JobDescriptions): Future[Long] = {
    val insertQuery = JobDescriptionsQuery returning JobDescriptionsQuery.map(_.jobId) += jobDescriptions

    db.run(insertQuery)
  }

  override def removeJob(jobId: Long): Future[Unit] = {
    val removeJobQuery = JobDescriptionsQuery
      .filter(_.jobId === jobId)
      .delete

    db.run(removeJobQuery).map(_ => ())
  }

  override def getJobById(jobId: Long): Future[JobDescriptions] = {
    val query = JobDescriptionsQuery
      .filter(_.jobId === jobId)

    db.run(query.result.head)
  }

}
