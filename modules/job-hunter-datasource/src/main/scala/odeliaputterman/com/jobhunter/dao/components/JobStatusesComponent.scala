package odeliaputterman.com.jobhunter.dao.components

import odeliaputterman.com.jobhunter.model.{JobInfo, JobStatus}
import slick.lifted.ProvenShape

import java.time.OffsetDateTime

class JobStatusesComponent {

  import odeliaputterman.com.jobhunter.dao.profile.JobHunterSlickProfile.api._

  val JobStatusesQuery = TableQuery[JobStatusesTable]

  class JobStatusesTable(tag: Tag) extends Table[JobInfo](tag, "job_statuses") {

    def userId: Rep[Long] = column[Long]("user_id")
    def jobId: Rep[Long] = column[Long]("job_id", O.AutoInc)
    def company: Rep[String] = column[String]("company")
    def jobTitle: Rep[String] = column[String]("job_title")
    def postingUrl: Rep[String] = column[String]("posting_url")
    def status: Rep[JobStatus] = column[JobStatus]("status")
    def lastUpdate: Rep[OffsetDateTime] = column[OffsetDateTime]("last_update")
    def interviewRound: Rep[Int] = column[Int]("interview_round")
    def notes: Rep[Option[String]] = column[Option[String]]("notes")

    def * : ProvenShape[JobInfo] = (
      userId,
      jobId,
      company,
      jobTitle,
      postingUrl,
      status,
      lastUpdate,
      interviewRound,
      notes
    ) <> (JobInfo.tupled, JobInfo.unapply)

  }

}
