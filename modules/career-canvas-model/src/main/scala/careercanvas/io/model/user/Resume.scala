package careercanvas.io.model.user

import java.sql.Timestamp

case class Resume(
  userId: Long,
  resumeId: Long,
  name: String,
  locationPath: String,
  uploadDate: Timestamp
)
