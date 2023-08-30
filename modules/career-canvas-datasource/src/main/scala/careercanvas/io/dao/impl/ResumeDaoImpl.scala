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

  override def register(resume: Resume): Future[Long] = {
    val insertQuery = ResumeQuery returning ResumeQuery.map(_.resumeId) += resume

    db.run(insertQuery)
  }

  override def delete(resumeId: Long): Future[Unit] = {
    val deleteQuery = ResumeQuery
      .filter(_.resumeId === resumeId)
      .delete

    db.run(deleteQuery).map(_ => ())
  }

}
