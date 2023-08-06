package careercanvas.io.dao.impl

import careercanvas.io.JobStatisticsDao
import careercanvas.io.dao.components.StatusPercentagesComponent
import careercanvas.io.model.StatusPercentage
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/**
 * dao to interact with the database to access job statistics
 * information. All the validation for calling these methods
 * should happen downstream.
 */
class JobStatisticsDaoImpl @Inject() (
  protected val dbConfigProvider: DatabaseConfigProvider
)(implicit ec: ExecutionContext)
  extends JobStatisticsDao
  with StatusPercentagesComponent
  with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  override def getStatusPercentages(userId: Long): Future[Seq[StatusPercentage]] = {
    val statusPercentagesQuery = StatusPercentageQuery
      .filter(_.userId === userId)

    db.run(statusPercentagesQuery.result)
  }

}
