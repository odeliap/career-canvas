package careercanvas.io.dao.components

import careercanvas.io.model.job.{JobInfo, JobStatus, JobType}
import slick.lifted.ProvenShape

import java.sql.Timestamp

trait JobStatusesComponent {

  import careercanvas.io.dao.profile.CareerCanvasSlickProfile.api._

  val JobStatusesQuery = TableQuery[JobStatusesTable]

  class JobStatusesTable(tag: Tag) extends Table[JobInfo](tag, "job_statuses") {

    def userId: Rep[Long] = column[Long]("user_id")
    def jobId: Rep[Long] = column[Long]("job_id")
    def postingUrl: Rep[String] = column[String]("posting_url")
    def company: Rep[String] = column[String]("company")
    def jobTitle: Rep[String] = column[String]("job_title")
    def jobType: Rep[JobType] = column[JobType]("jobtype")
    def location: Rep[String] = column[String]("location")
    def salaryRange: Rep[String] = column[String]("salary_range")
    def status: Rep[JobStatus] = column[JobStatus]("status")
    def appSubmissionDate: Rep[Option[Timestamp]] = column[Option[Timestamp]]("app_submission_date")
    def dateAdded: Rep[Timestamp] = column[Timestamp]("date_added")
    def lastUpdate: Rep[Timestamp] = column[Timestamp]("last_update")
    def notes: Rep[Option[String]] = column[Option[String]]("notes")
    def starred: Rep[Boolean] = column[Boolean]("starred")

    def * : ProvenShape[JobInfo] = (
      userId,
      jobId,
      postingUrl,
      company,
      jobTitle,
      jobType,
      location,
      salaryRange,
      status,
      appSubmissionDate,
      dateAdded,
      lastUpdate,
      notes,
      starred
    ) <> ((JobInfo.apply _).tupled, JobInfo.unapply)

  }

}
