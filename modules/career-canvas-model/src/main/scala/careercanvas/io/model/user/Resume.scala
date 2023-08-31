package careercanvas.io.model.user

import java.sql.Timestamp

case class Resume(
  userId: Long,
  version: Int,
  name: String,
  bucket: String,
  prefix: String,
  uploadDate: Timestamp
)
