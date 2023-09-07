package careercanvas.io.model.job

import java.sql.Timestamp

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
