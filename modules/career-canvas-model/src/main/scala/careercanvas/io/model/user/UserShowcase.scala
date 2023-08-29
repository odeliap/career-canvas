package careercanvas.io.model.user

case class UserShowcase(
                         userId: String,
                         resume: Option[String],
                         linkedIn: Option[String],
                         gitHub: Option[String],
                         website: Option[String]
                       )
