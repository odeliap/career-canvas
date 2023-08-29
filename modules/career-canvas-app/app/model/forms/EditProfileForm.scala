package model.forms

case class EditProfileForm(
                            resume: Option[String],
                            linkedIn: Option[String],
                            gitHub: Option[String],
                            website: Option[String],
                            email: Option[String],
                            name: Option[String]
                          )
