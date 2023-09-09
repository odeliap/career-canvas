package careercanvas.io.model.metrics

case class MetricsDao(
  statusPercentages: Seq[StatusPercentage],
  searchDuration: Long,
  totalApplications: Int,
  totalOffers: Int,
  totalRejections: Int
)
