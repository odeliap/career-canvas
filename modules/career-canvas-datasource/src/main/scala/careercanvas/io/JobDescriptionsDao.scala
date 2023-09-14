package careercanvas.io

import careercanvas.io.model.job.JobDescriptions

import scala.concurrent.Future

trait JobDescriptionsDao {

  def addJob(jobDescriptions: JobDescriptions): Future[Unit]

  def removeJob(jobId: Long): Future[Unit]

  def getJobById(jobId: Long): Future[JobDescriptions]

}
