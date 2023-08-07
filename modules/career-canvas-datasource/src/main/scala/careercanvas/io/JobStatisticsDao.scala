package careercanvas.io

import careercanvas.io.model.StatusPercentage

import scala.concurrent.Future

trait JobStatisticsDao {

  def getStatusPercentages(userId: Long): Future[Seq[StatusPercentage]]

}