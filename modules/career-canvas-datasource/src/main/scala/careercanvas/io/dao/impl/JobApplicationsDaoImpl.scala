package careercanvas.io.dao.impl

import careercanvas.io.JobApplicationsDao
import careercanvas.io.dao.components.JobStatusesComponent
import careercanvas.io.model.job.{JobInfo, UpdateJobInfo}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/**
 * dao to interact with the database to access jobs
 * information. All the validation for calling these methods
 * should happen downstream.
 */
class JobApplicationsDaoImpl @Inject() (
  protected val dbConfigProvider: DatabaseConfigProvider
) (implicit ec: ExecutionContext)
  extends JobApplicationsDao
  with JobStatusesComponent
  with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  override def addJob(jobInfo: JobInfo): Future[Long] = {
    val insertQuery = JobStatusesQuery returning JobStatusesQuery.map(_.jobId) += jobInfo

    db.run(insertQuery)
  }

  override def updateJobStatus(updateJobInfo: UpdateJobInfo): Future[Unit] = {
    val jobInfoQuery = JobStatusesQuery
      .filter(_.jobId === updateJobInfo.jobId)

    val tx = for {
      jobInfo <- jobInfoQuery.result.head

      _ <- jobInfoQuery.update(jobInfo.patch(
        updateJobInfo
      ))
    } yield ()

    db.run(tx.transactionally).map(_ => ())
  }

  override def removeJob(userId: Long, jobId: Long): Future[Unit] = {
    val removeJobQuery = JobStatusesQuery
      .filter(row => row.userId === userId && row.jobId === jobId)
      .delete

    db.run(removeJobQuery).map(_ => ())
  }

  override def getJobById(userId: Long, jobId: Long): Future[JobInfo] = {
    val query = JobStatusesQuery
      .filter(row => row.userId === userId && row.jobId === jobId)

    db.run(query.result.head)
  }

  override def getJobs(userId: Long): Future[Seq[JobInfo]] = {
    val baseQuery = JobStatusesQuery.filter(_.userId === userId).sortBy(_.dateAdded.desc)

    db.run(baseQuery.result)
  }

}
