package careercanvas.io.model.job

import play.api.mvc.QueryStringBindable

import java.sql.Timestamp
import java.time.{Instant, ZoneId, ZonedDateTime}
import scala.util.Try

case class JobInfo(
  userId: Long,
  jobId: Long,
  company: String,
  jobTitle: String,
  postUrl: String,
  status: JobStatus,
  appSubmissionDate: Option[Timestamp],
  lastUpdate: Timestamp = Timestamp.from(Instant.now()),
  interviewRound: Option[Int] = Option(0),
  notes: Option[String] = None,
  starred: Boolean = false
) {

  def patch(updateJobInfo: UpdateJobInfo): JobInfo = {
    this.copy(
      status = updateJobInfo.status.getOrElse(this.status),
      appSubmissionDate = updateJobInfo.appSubmissionDate.orElse(this.appSubmissionDate),
      interviewRound = updateJobInfo.interviewRound.orElse(this.interviewRound),
      notes = updateJobInfo.notes.orElse(this.notes),
      starred = updateJobInfo.starred.getOrElse(this.starred)
    )
  }

}

object JobInfo {

  implicit object bindable extends QueryStringBindable[JobInfo] {

    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, JobInfo]] = Option {
      Try {
        val userId = params("userId").head.toLong
        val jobId = params("jobId").head.toLong
        val company = params("company").head
        val jobTitle = params("jobTitle").head
        val postUrl = params("postUrl").head
        val status = JobStatus.stringToEnum(params("status").head)
        val appSubmissionDate = params.get("appSubmissionDate").flatMap(seq =>
          if(seq.nonEmpty && seq.head.nonEmpty) {
            Some(Timestamp.from(ZonedDateTime.parse(seq.head).toInstant))
          } else {
            None
          }
        )
        val lastUpdate = params.get("lastUpdate").map(_.head).map { dateStr =>
          Timestamp.from(ZonedDateTime.parse(dateStr).toInstant)
        }.getOrElse(Timestamp.from(Instant.now()))
        val interviewRound = params.get("interviewRound").flatMap(seq =>
          if (seq.nonEmpty && seq.head.nonEmpty) {
            Some(seq.head.toInt)
          } else {
            None
          }
        )
        val notes = params.get("notes").map(_.head)
        val starred = params.get("starred").map(_.head).exists(_.toBoolean)

        Right(JobInfo(userId, jobId, company, jobTitle, postUrl, status, appSubmissionDate, lastUpdate, interviewRound, notes, starred))
      }.getOrElse(Left("Unable to bind JobInfo"))
    }

    override def unbind(key: String, jobInfo: JobInfo): String = {
      val appSubmissionDate = jobInfo.appSubmissionDate.map(_.toInstant.atZone(ZoneId.systemDefault()).toString).getOrElse("")
      val lastUpdate = jobInfo.lastUpdate.toInstant.atZone(ZoneId.systemDefault()).toString
      val interviewRound = jobInfo.interviewRound.map(_.toString).getOrElse("")
      val notes = jobInfo.notes.getOrElse("")

      s"userId=${jobInfo.userId}&jobId=${jobInfo.jobId}&company=${jobInfo.company}&jobTitle=${jobInfo.jobTitle}&postUrl=${jobInfo.postUrl}&status=${jobInfo.status}&appSubmissionDate=$appSubmissionDate&lastUpdate=$lastUpdate&interviewRound=$interviewRound&notes=$notes&starred=${jobInfo.starred}"
    }
  }
}
