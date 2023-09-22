package careercanvas.io

import careercanvas.io.model.job.ApplicationFile

import scala.concurrent.Future

trait JobApplicationFilesDao {

  def add(applicationFile: ApplicationFile): Future[Long]

  def getFilesByJob(userId: Long, jobId: Long): Future[Seq[ApplicationFile]]

  def delete(userId: Long, jobId: Long, fileId: Long): Future[Unit]

}
