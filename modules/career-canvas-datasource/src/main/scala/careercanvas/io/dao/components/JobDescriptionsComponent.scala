package careercanvas.io.dao.components

import careercanvas.io.model.job.JobDescriptions
import slick.lifted.ProvenShape

trait JobDescriptionsComponent {

  import careercanvas.io.dao.profile.CareerCanvasSlickProfile.api._

  val JobDescriptionsQuery = TableQuery[JobDescriptionsTable]

  class JobDescriptionsTable(tag: Tag) extends Table[JobDescriptions](tag, "job_descriptions") {

    def jobId: Rep[Long] = column[Long]("job_id")
    def jobDescription: Rep[String] = column[String]("job_description")
    def companyDescription: Rep[String] = column[String]("company_description")

    def * : ProvenShape[JobDescriptions] = (
      jobId,
      jobDescription,
      companyDescription
    ) <> (JobDescriptions.tupled, JobDescriptions.unapply)

  }

}
