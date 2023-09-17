package careercanvas.io.dao.impl

import careercanvas.io.JobStatisticsDao
import careercanvas.io.dao.components.JobStatusesComponent
import careercanvas.io.model.job.JobStatus
import careercanvas.io.model.metrics.{MetricsDao, StatusPercentage}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import java.sql.Timestamp
import java.time.LocalDateTime
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
  with JobStatusesComponent
  with HasDatabaseConfigProvider[JdbcProfile] {

  import careercanvas.io.dao.profile.CareerCanvasSlickProfile.api._

  override def calculateMetrics(userId: Long): Future[MetricsDao] = {
    val userQuery = JobStatusesQuery
      .filter(_.userId === userId)

    val tx = for {
      earliestApplicationDate <- userQuery.result.map(results => results.map(_.dateAdded).min)

      totalRows <- userQuery.result.map(_.size)

      statusGroupings <- userQuery
        .groupBy(_.status)
        .map { case (status, rows) =>
          (status, rows.size)
        }.result
    } yield (earliestApplicationDate, totalRows, statusGroupings)

    db
      .run(tx.transactionally)
      .map{ case (earliestApplicationDate, totalApplications, statusGroupings) =>
        val statusPercentages = statusGroupingsToPercentages(statusGroupings, totalApplications)
        val totalOffers = statusGroupingsToStatusCount(statusGroupings, JobStatus.Offer)
        val totalRejections = statusGroupingsToStatusCount(statusGroupings, JobStatus.Rejected)
        val earliestAppEpochDays = earliestApplicationDate.toLocalDateTime.toLocalDate.toEpochDay
        val todayEpochDays = LocalDateTime.now().toLocalDate.toEpochDay
        val searchDuration = todayEpochDays - earliestAppEpochDays
        MetricsDao(
          statusPercentages,
          searchDuration,
          totalApplications,
          totalOffers,
          totalRejections
        )
      }
  }

  private def statusGroupingsToPercentages(statusGroupings: Seq[(JobStatus, Int)], totalApplications: Int): Seq[StatusPercentage] = {
    statusGroupings.map { case (status, rowsCount) =>
      StatusPercentage(status, (rowsCount.toDouble/totalApplications.toDouble) * 100.0)
    }
  }

  private def statusGroupingsToStatusCount(statusGroupings: Seq[(JobStatus, Int)], jobStatus: JobStatus): Int = {
    statusGroupings
      .find { case (status, _) => status == jobStatus }
      .map { case (_, count) => count }
      .getOrElse(0)
  }

}
