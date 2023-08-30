package careercanvas.io

import careercanvas.io.model.user.Resume

import scala.concurrent.Future

trait ResumeDao {

  def register(resume: Resume): Future[Long]

  def getAll(userId: Long): Future[Seq[Resume]]

  def delete(resumeId: Long): Future[Unit]

}
