package service

import careercanvas.io.JobStatisticsDao
import careercanvas.io.model.metrics.{MetricsDao, StatusPercentage}
import careercanvas.io.util.AwaitResult

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class JobStatisticsService @Inject() (
  jobStatisticsDao: JobStatisticsDao
)(implicit ec: ExecutionContext)
  extends AwaitResult {

  def getMetrics(userId: String): MetricsDao = {
    jobStatisticsDao
      .calculateMetrics(userId.toLong)
      .waitForResult
  }

  def cleanStatusBreakdown(statusPercentages: Seq[StatusPercentage]): Seq[(String, Double)] = {
    statusPercentages
      .map(statusPercentage => (
        statusPercentage.status.toString.replaceAll("\\d+", "").replaceAll("(.)([A-Z])", "$1 $2"),
        statusPercentage.percentage)
      )
  }

}
