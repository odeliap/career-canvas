package careercanvas.io.dao.components

import careercanvas.io.model.jobfeed.JobStatus
import careercanvas.io.model.metrics.StatusPercentage
import slick.lifted.ProvenShape

trait StatusPercentagesComponent {

  import careercanvas.io.dao.profile.CareerCanvasSlickProfile.api._

  val StatusPercentageQuery = TableQuery[StatusPercentageTable]

  class StatusPercentageTable(tag: Tag) extends Table[StatusPercentage](tag, "job_status_breakdown") {

    def userId: Rep[Long] = column[Long]("user_id")
    def status: Rep[JobStatus] = column[JobStatus]("status")
    def percentage: Rep[Long] = column[Long]("percentage")

    def * : ProvenShape[StatusPercentage] = (
      userId,
      status,
      percentage
    ) <> (StatusPercentage.tupled, StatusPercentage.unapply)

  }

}
