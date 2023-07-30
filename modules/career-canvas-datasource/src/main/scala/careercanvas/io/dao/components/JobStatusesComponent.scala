package careercanvas.io.dao.components

import careercanvas.io.model.{JobInfo, JobStatus}
import slick.lifted.ProvenShape

import java.sql.Timestamp

trait JobStatusesComponent {

  import careercanvas.io.dao.profile.CareerCanvasSlickProfile.api._

  val JobStatusesQuery = TableQuery[JobStatusesTable]

  class JobStatusesTable(tag: Tag) extends Table[JobInfo](tag, "job_statuses") {

    def jobId: Rep[Long] = column[Long]("job_id", O.AutoInc)
    def userId: Rep[Long] = column[Long]("user_id")
    def company: Rep[String] = column[String]("company")
    def jobTitle: Rep[String] = column[String]("job_title")
    def postingUrl: Rep[String] = column[String]("posting_url")
    def status: Rep[JobStatus] = column[JobStatus]("status")
    def appSubmissionDate: Rep[Option[Timestamp]] = column[Option[Timestamp]]("app_submission_date")
    def lastUpdate: Rep[Timestamp] = column[Timestamp]("last_update")
    def interviewRound: Rep[Option[Int]] = column[Option[Int]]("interview_round")
    def notes: Rep[Option[String]] = column[Option[String]]("notes")

    def * : ProvenShape[JobInfo] = (
      jobId,
      userId,
      company,
      jobTitle,
      postingUrl,
      status,
      appSubmissionDate,
      lastUpdate,
      interviewRound,
      notes
    ) <> (JobInfo.tupled, JobInfo.unapply)

  }

}
