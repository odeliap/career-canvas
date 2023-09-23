package careercanvas.io.model.job

import play.api.libs.json._
import play.api.libs.json.Reads
import play.api.libs.functional.syntax._
import play.api.mvc.QueryStringBindable

import java.sql.Timestamp
import java.time.{Instant, ZoneId, ZonedDateTime}
import scala.util.Try

case class JobInfo(
  userId: Long,
  jobId: Long,
  postUrl: String,
  company: String,
  jobTitle: String,
  jobType: JobType,
  location: String,
  salaryRange: String,
  status: JobStatus,
  appSubmissionDate: Option[Timestamp],
  dateAdded: Timestamp = Timestamp.from(Instant.now()),
  lastUpdate: Timestamp = Timestamp.from(Instant.now()),
  starred: Boolean = false
) {

  def patch(updateJobInfo: UpdateJobInfo): JobInfo = {
    this.copy(
      jobType = updateJobInfo.jobType.getOrElse(this.jobType),
      status = updateJobInfo.status.getOrElse(this.status),
      appSubmissionDate = updateJobInfo.appSubmissionDate.orElse(this.appSubmissionDate),
      dateAdded = updateJobInfo.dateAdded.getOrElse(this.dateAdded),
      starred = updateJobInfo.starred.getOrElse(this.starred),
      lastUpdate = updateJobInfo.lastUpdate
    )
  }

}

object JobInfo {

  implicit val timestampReads: Reads[Timestamp] = Reads { json =>
    json.validate[String].map(str => {
      Timestamp.from(ZonedDateTime.parse(str).toInstant)
    })
  }

  implicit val timestampWrites: Writes[Timestamp] = Writes { ts =>
    JsString(ts.toInstant.atZone(ZoneId.systemDefault()).toString)
  }

  implicit val jobInfoWrites: Writes[JobInfo] = (
    (JsPath \ "userId").write[Long] and
      (JsPath \ "jobId").write[Long] and
      (JsPath \ "postUrl").write[String] and
      (JsPath \ "company").write[String] and
      (JsPath \ "jobTitle").write[String] and
      (JsPath \ "jobType").write[JobType] and
      (JsPath \ "location").write[String] and
      (JsPath \ "salaryRange").write[String] and
      (JsPath \ "status").write[JobStatus] and
      (JsPath \ "appSubmissionDate").writeNullable[Timestamp] and
      (JsPath \ "dateAdded").write[Timestamp] and
      (JsPath \ "lastUpdate").write[Timestamp] and
      (JsPath \ "starred").write[Boolean]
    )(unlift(JobInfo.unapply))

  implicit val jobInfoReads: Reads[JobInfo] = (
    (__ \ "userId").read[Long] and
      (__ \ "jobId").read[Long] and
      (__ \ "postUrl").read[String] and
      (__ \ "company").read[String] and
      (__ \ "jobTitle").read[String] and
      (__ \ "jobType").read[JobType] and
      (__ \ "location").read[String] and
      (__ \ "salaryRange").read[String] and
      (__ \ "status").read[JobStatus] and
      (__ \ "appSubmissionDate").readNullable[Timestamp] and
      (__ \ "dateAdded").read[Timestamp] and
      (__ \ "lastUpdate").read[Timestamp] and
      (__ \ "starred").read[Boolean]
    )(JobInfo.apply _)

  implicit object bindable extends QueryStringBindable[JobInfo] {

    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, JobInfo]] = {
      Option {
        Try {
          val userId = params.get("userId").flatMap(_.headOption).map(_.toLong).getOrElse(throw new Exception("Missing userId"))
          val jobId = params.get("jobId").flatMap(_.headOption).map(_.toLong).getOrElse(throw new Exception("Missing jobId"))
          val postUrl = params.get("postUrl").flatMap(_.headOption).getOrElse(throw new Exception("Missing postUrl"))
          val company = params.get("company").flatMap(_.headOption).getOrElse(throw new Exception("Missing company"))
          val jobTitle = params.get("jobTitle").flatMap(_.headOption).getOrElse(throw new Exception("Missing jobTitle"))
          val jobType = params.get("jobType").flatMap(_.headOption).map(JobType.stringToEnum).getOrElse(throw new Exception("Missing job type"))
          val location = params.get("location").flatMap(_.headOption).getOrElse(throw new Exception("Missing location"))
          val salaryRange = params.get("salaryRange").flatMap(_.headOption).getOrElse(throw new Exception("Missing salary range"))
          val status = params.get("status").flatMap(_.headOption).map(JobStatus.stringToEnum).getOrElse(throw new Exception("Missing status"))

          val appSubmissionDate = params.get("appSubmissionDate").flatMap(_.headOption).flatMap { dateStr =>
            if(dateStr.nonEmpty) {
              Some(Timestamp.from(ZonedDateTime.parse(dateStr).toInstant))
            } else {
              None
            }
          }

          val dateAdded = params.get("dateAdded").flatMap(_.headOption).map { dateStr =>
            Timestamp.from(ZonedDateTime.parse(dateStr).toInstant)
          }.getOrElse(Timestamp.from(Instant.now()))

          val lastUpdate = params.get("lastUpdate").flatMap(_.headOption).map { dateStr =>
            Timestamp.from(ZonedDateTime.parse(dateStr).toInstant)
          }.getOrElse(Timestamp.from(Instant.now()))

          val starred = params.get("starred").flatMap(_.headOption).exists(_.toBoolean)

          Right(JobInfo(userId, jobId, postUrl, company, jobTitle, jobType, location, salaryRange, status, appSubmissionDate, dateAdded, lastUpdate, starred))
        }.getOrElse(Left("Unable to bind JobInfo"))
      }
    }

    override def unbind(key: String, jobInfo: JobInfo): String = {
      val appSubmissionDate = jobInfo.appSubmissionDate.map(_.toInstant.atZone(ZoneId.systemDefault()).toString).getOrElse("")
      val dateAdded = jobInfo.dateAdded.toInstant.atZone(ZoneId.systemDefault()).toString
      val lastUpdate = jobInfo.lastUpdate.toInstant.atZone(ZoneId.systemDefault()).toString

      s"userId=${jobInfo.userId}&jobId=${jobInfo.jobId}&postUrl=${jobInfo.postUrl}&company=${jobInfo.company}&jobTitle=${jobInfo.jobTitle}&jobType=${jobInfo.jobType}&location=${jobInfo.location}&salaryRange=${jobInfo.salaryRange}&status=${jobInfo.status}&appSubmissionDate=$appSubmissionDate&dateAdded=${dateAdded}&lastUpdate=$lastUpdate&starred=${jobInfo.starred}"
    }
  }
}
