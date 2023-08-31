package careercanvas.io.dao.impl

import careercanvas.io.ResumeDao
import careercanvas.io.converter.Converters
import careercanvas.io.dao.components.ResumeComponent
import careercanvas.io.model.user.Resume
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.{ ExecutionContext, Future }

/**
 * dao to interact with the database to access resume
 * information. All the validation for calling these methods
 * should happen downstream.
 */
class ResumeDaoImpl @Inject() (
  protected val dbConfigProvider: DatabaseConfigProvider
) (implicit ec: ExecutionContext)
  extends ResumeDao
  with ResumeComponent
  with HasDatabaseConfigProvider[JdbcProfile]
  with Converters {

  import profile.api._

  override def register(resume: Resume): Future[Int] = {
    val insertQuery = ResumeQuery returning ResumeQuery.map(_.version) += resume

    db.run(insertQuery)
  }

  override def getLatest(userId: Long): Future[Option[Resume]] = {
    val query = ResumeQuery
      .filter(_.userId === userId)
      .sortBy(_.version.desc)

    db.run(query.result.headOption)
  }

  override def getByVersion(userId: Long, version: Int): Future[Resume] = {
    val query = ResumeQuery
      .filter(resume => resume.userId === userId && resume.version === version)

    db.run(query.result.head)
  }

  override def getAll(userId: Long): Future[Seq[Resume]] = {
    val resumesQuery = ResumeQuery
      .filter(_.userId === userId)

    db.run(resumesQuery.result)
  }

  override def delete(userId: Long, version: Int): Future[Unit] = {
    val deleteQuery = ResumeQuery
      .filter(resume => resume.userId === userId && resume.version === version)
      .delete

    db.run(deleteQuery).map(_ => ())
  }

}
