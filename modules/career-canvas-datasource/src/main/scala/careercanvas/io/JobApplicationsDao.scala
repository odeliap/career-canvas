package careercanvas.io

import careercanvas.io.model.feed.{JobInfo, UpdateJobInfo}

import scala.concurrent.Future

trait JobApplicationsDao {

  def addJob(jobInfo: JobInfo): Future[Long]

  def updateJobStatus(updateJobInfo: UpdateJobInfo): Future[Unit]

  def removeJob(userId: Long, jobId: Long): Future[Unit]

  def getJobs(userId: Long): Future[Seq[JobInfo]]

}
