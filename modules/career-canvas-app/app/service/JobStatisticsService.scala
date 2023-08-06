package service

import careercanvas.io.JobStatisticsDao
import careercanvas.io.util.AwaitResult

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class JobStatisticsService @Inject() (
  jobStatisticsDao: JobStatisticsDao
)(implicit ec: ExecutionContext)
  extends AwaitResult {

  def getStatusBreakdown(userId: String): Seq[(String, Long)] = {
    jobStatisticsDao
      .getStatusPercentages(userId.toLong)
      .waitForResult
      .map(statusPercentage => (
        statusPercentage.status.toString.replaceAll("\\d+", "").replaceAll("(.)([A-Z])", "$1 $2"),
        statusPercentage.percentage)
      )
  }

}
