package controllers

import authentication.AuthenticatedUserMessagesAction
import careercanvas.io.model.user.UserInfo
import model.Global
import model.forms.EditProfileForm
import play.api.data.Form
import play.api.data.Forms._

import javax.inject._
import play.api.mvc._
import service.ProfileService

@Singleton
class ProfileController @Inject()(
  cc: MessagesControllerComponents,
  profileService: ProfileService,
  authenticatedUserMessagesAction: AuthenticatedUserMessagesAction
) extends MessagesAbstractController(cc) {

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

  def editProfileUrl(userInfo: UserInfo): Call = routes.ProfileController.processEditProfileAttempt(userInfo)

  def editProfile(): Action[AnyContent] = authenticatedUserMessagesAction { implicit request: MessagesRequest[AnyContent] =>
    val userId = request.session.data(Global.SESSION_USER_ID)
    val userInfo = profileService.getUserInfo(userId)
    Ok(views.html.authenticated.user.profile.editProfile(userInfo, editProfileForm, editProfileUrl(userInfo)))
  }

  def processEditProfileAttempt(userInfo: UserInfo): Action[AnyContent] = Action { implicit request: MessagesRequest[AnyContent] =>
    val errorFunction = { formWithErrors: Form[EditProfileForm] =>
      // form validation/binding failed...
      BadRequest(views.html.authenticated.user.profile.editProfile(userInfo, formWithErrors, editProfileUrl(userInfo)))
    }
    val successFunction = { editProfileForm: EditProfileForm =>
      val userId = request.session.data(Global.SESSION_USER_ID)
      profileService.updateUserProfile(userId, editProfileForm)
      Redirect(routes.ProfileController.editProfile())
        .withSession(request.session)
        .flashing("success" -> "Profile information updated.")
    }
    val formValidationResult: Form[EditProfileForm] = editProfileForm.bindFromRequest
    formValidationResult.fold(
      errorFunction,
      successFunction
    )
  }

  def logout: Action[AnyContent] = authenticatedUserMessagesAction { implicit request: MessagesRequest[AnyContent] =>
    Redirect(routes.UserController.showLoginForm())
      .flashing("info" -> "You are logged out.")
      .withNewSession
  }

}
