package careercanvas.io

import careercanvas.io.model.metrics.MetricsDao

import scala.concurrent.Future

trait JobStatisticsDao {

  def calculateMetrics(userId: Long): Future[MetricsDao]

}
