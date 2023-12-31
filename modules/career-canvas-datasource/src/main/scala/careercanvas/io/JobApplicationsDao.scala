package careercanvas.io

import careercanvas.io.model.job._

import scala.concurrent.Future

trait JobApplicationsDao {

  def addJob(jobInfo: JobInfo): Future[Long]

  def updateJobStatus(updateJobInfo: UpdateJobInfo): Future[Unit]

  def removeJob(userId: Long, jobId: Long): Future[Unit]

  def getJobById(userId: Long, jobId: Long): Future[JobInfo]

  def getJobs(userId: Long): Future[Seq[JobInfo]]

}
