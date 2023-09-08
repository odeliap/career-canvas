package careercanvas.io.model.job

import play.api.mvc.QueryStringBindable
import play.api.libs.json._

import java.time.{Instant, ZoneId, ZonedDateTime}
import java.sql.Timestamp
import scala.util.Try

case class ApplicationFile(
  userId: Long,
  jobId: Long,
  fileId: Long,
  name: String,
  fileType: ApplicationFileType,
  bucket: String,
  prefix: String,
  uploadDate: Timestamp
)

object ApplicationFile {

  implicit val timestampFormat: Format[Timestamp] = new Format[Timestamp] {
    def reads(json: JsValue): JsResult[Timestamp] = {
      val str = json.as[String]
      JsSuccess(Timestamp.valueOf(str))
    }

    def writes(ts: Timestamp): JsValue = JsString(ts.toString)
  }

  implicit val applicationFileFormat: Format[ApplicationFile] = Json.format[ApplicationFile]
  implicit val applicationFileWrites: Writes[ApplicationFile] = Json.writes[ApplicationFile]
  implicit val applicationFileReads: Reads[ApplicationFile] = Json.reads[ApplicationFile]

  implicit object bindable extends QueryStringBindable[ApplicationFile] {

    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, ApplicationFile]] = Option {
      Try {
        val userId = params("userId").head.toLong
        val jobId = params("jobId").head.toLong
        val name = params("name").head

        val fileId = params.get("fileId").map(_.head.toLong).getOrElse(0L)
        val fileType = params.get("fileType").map(_.head).map(ApplicationFileType.stringToEnum).getOrElse(ApplicationFileType.default)
        val bucket = params.get("bucket").map(_.head).getOrElse("defaultBucket")
        val prefix = params.get("prefix").map(_.head).getOrElse("defaultPrefix")
        val uploadDate = params.get("uploadDate").map(_.head).map { dateStr =>
          Timestamp.from(ZonedDateTime.parse(dateStr).toInstant)
        }.getOrElse(Timestamp.from(Instant.now()))

        Right(ApplicationFile(userId, jobId, fileId, name, fileType, bucket, prefix, uploadDate))
      }.getOrElse(Left("Unable to bind ApplicationFile"))
    }


    override def unbind(key: String, appFile: ApplicationFile): String = {
      val uploadDate = appFile.uploadDate.toInstant.atZone(ZoneId.systemDefault()).toString

      s"userId=${appFile.userId}&jobId=${appFile.jobId}&fileId=${appFile.fileId}&name=${appFile.name}&fileType=${appFile.fileType}&bucket=${appFile.bucket}&prefix=${appFile.prefix}&uploadDate=$uploadDate"
    }
  }
}
