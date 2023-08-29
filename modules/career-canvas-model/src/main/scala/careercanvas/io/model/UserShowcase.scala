package careercanvas.io.model

case class UserShowcase(
  userId: String,
  resume: Option[String],
  linkedIn: Option[String],
  gitHub: Option[String],
  website: Option[String]
)
