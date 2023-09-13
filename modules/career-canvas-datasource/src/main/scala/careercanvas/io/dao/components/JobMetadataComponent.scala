package careercanvas.io.dao.components

import careercanvas.io.model.job.JobMetadata
import slick.lifted.ProvenShape

trait JobMetadataComponent {

  import careercanvas.io.dao.profile.CareerCanvasSlickProfile.api._

  val JobMetadataQuery = TableQuery[JobMetadataTable]

  class JobMetadataTable(tag: Tag) extends Table[JobMetadata](tag, "job_metadata") {

    def jobId: Rep[Long] = column[Long]("job_id")
    def location: Rep[String] = column[String]("location")
    def salary: Rep[String] = column[String]("salary")
    def jobDescription: Rep[String] = column[String]("job_description")
    def companyDescription: Rep[String] = column[String]("company_description")

    def * : ProvenShape[JobMetadata] = (
      jobId,
      location,
      salary,
      jobDescription,
      companyDescription
    ) <> (JobMetadata.tupled, JobMetadata.unapply)

  }

}
