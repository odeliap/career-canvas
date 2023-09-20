package careercanvas.io.dao.components

import careercanvas.io.model.job.JobDescriptions
import slick.lifted.ProvenShape

trait JobDescriptionsComponent {

  import careercanvas.io.dao.profile.CareerCanvasSlickProfile.api._

  val JobDescriptionsQuery = TableQuery[JobDescriptionsTable]

  class JobDescriptionsTable(tag: Tag) extends Table[JobDescriptions](tag, "job_descriptions") {

    def jobId: Rep[Long] = column[Long]("job_id", O.AutoInc)
    def about: Rep[String] = column[String]("about")
    def requirements: Rep[String] = column[String]("requirements")
    def techStack: Rep[String] = column[String]("tech_stack")
    def notes: Rep[Option[String]] = column[Option[String]]("notes")

    def * : ProvenShape[JobDescriptions] = (
      jobId,
      about,
      requirements,
      techStack,
      notes
    ) <> (JobDescriptions.tupled, JobDescriptions.unapply)

  }

}
