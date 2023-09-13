package careercanvas.io

import careercanvas.io.model.job.JobMetadata

import scala.concurrent.Future

trait JobMetadataDao {

  def addJob(jobMetadata: JobMetadata): Future[Unit]

  def removeJob(jobId: Long): Future[Unit]

  def getJobById(jobId: Long): Future[JobMetadata]

}
