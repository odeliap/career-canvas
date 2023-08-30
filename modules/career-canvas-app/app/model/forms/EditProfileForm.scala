package model.forms

case class EditProfileForm(
                            resumeId: Option[Long],
                            linkedIn: Option[String],
                            gitHub: Option[String],
                            website: Option[String],
                            email: Option[String],
                            name: Option[String]
                          )
