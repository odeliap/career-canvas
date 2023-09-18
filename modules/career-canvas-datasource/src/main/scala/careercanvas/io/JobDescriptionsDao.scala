package careercanvas.io

import careercanvas.io.model.job.{JobDescriptions, UpdateJobDescriptions}

import scala.concurrent.Future

trait JobDescriptionsDao {

  def addJob(jobDescriptions: JobDescriptions): Future[Long]

  def updateJob(updateJobDescriptions: UpdateJobDescriptions): Future[Unit]

  def removeJob(jobId: Long): Future[Unit]

  def getJobById(jobId: Long): Future[JobDescriptions]

}
