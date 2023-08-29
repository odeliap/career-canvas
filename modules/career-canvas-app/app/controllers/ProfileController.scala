package controllers

import authentication.AuthenticatedUserAction
import careercanvas.io.model.user.UserShowcase
import model.Global
import model.forms.EditProfileForm
import play.api.data.Form
import play.api.data.Forms._

import javax.inject._
import play.api.mvc._
import service.ProfileService

@Singleton
class ProfileController @Inject()(
  cc: ControllerComponents,
  profileService: ProfileService,
  authenticatedUserAction: AuthenticatedUserAction
) extends AbstractController(cc) {

  private val logger = play.api.Logger(this.getClass)

  val editProfileForm: Form[EditProfileForm] = Form(
    mapping(
      "resume" -> optional(text),
      "linkedIn" -> optional(text),
      "gitHub" -> optional(text),
      "website" -> optional(text),
      "email" -> optional(text),
      "name" -> optional(text)
    )(EditProfileForm.apply)(EditProfileForm.unapply)
  )

  def editProfile(): Action[AnyContent] = authenticatedUserAction { implicit request: Request[AnyContent] =>
    val userId = request.session.data(Global.SESSION_USER_ID)
    val userEmail = request.session.data(Global.SESSION_USERNAME_KEY)
    val userName = request.session.data(Global.SESSION_USER_FULL_NAME)
    val userShowcase = profileService.getUserShowcase(userId)
    Ok(views.html.authenticated.user.profile.editProfile(userShowcase, userEmail, userName, editProfileForm))
  }

  def logout: Action[AnyContent] = authenticatedUserAction { implicit request: Request[AnyContent] =>
    Redirect(routes.UserController.showLoginForm())
      .flashing("info" -> "You are logged out.")
      .withNewSession
  }

}
