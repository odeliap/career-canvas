package odeliaputterman.com.jobhunter.dao.impl

import odeliaputterman.com.jobhunter.JobApplicationsDao
import odeliaputterman.com.jobhunter.model.{JobInfo, UpdateJobInfo}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class JobApplicationsDaoImpl @Inject() (
  protected val dbConfigProvider: DatabaseConfigProvider
) (implicit ec: ExecutionContext)
  extends JobApplicationsDao
  with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  override def addJob(jobInfo: JobInfo): Long = ???

  override def updateJobStatus(updateJobInfo: UpdateJobInfo): Boolean = ???

  override def removeJob(userId: String, jobId: Long): Unit = ???

}
