package careercanvas.io

import careercanvas.io.model.user.Resume

import scala.concurrent.Future

trait ResumeDao {

  def register(resume: Resume): Future[Int]

  def getLatest(userId: Long): Future[Option[Resume]]

  def getByVersion(userId: Long, version: Int): Future[Resume]

  def getAll(userId: Long): Future[Seq[Resume]]

  def delete(userId: Long, version: Int): Future[Unit]

}
