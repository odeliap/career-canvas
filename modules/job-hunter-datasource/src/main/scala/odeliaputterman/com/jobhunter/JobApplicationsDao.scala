package odeliaputterman.com.jobhunter

import odeliaputterman.com.jobhunter.model.{JobInfo, UpdateJobInfo}
import scala.concurrent.Future

trait JobApplicationsDao {

  def addJob(jobInfo: JobInfo): Future[Long]

  def updateJobStatus(updateJobInfo: UpdateJobInfo): Future[Unit]

  def removeJob(userId: Long, jobId: Long): Future[Unit]

}
