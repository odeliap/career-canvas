package careercanvas.io

import careercanvas.io.model.job.ApplicationFile

import scala.concurrent.Future

trait ApplicationFilesDao {

  def add(applicationFile: ApplicationFile): Future[Long]

  def delete(fileId: Long): Future[Unit]

}
