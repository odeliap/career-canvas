package controllers

import authentication.{AuthenticatedUserAction, AuthenticatedUserMessagesAction}
import careercanvas.io.model.user.UserInfo
import model.Global
import model.forms.EditProfileForm
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.Files

import javax.inject._
import play.api.mvc._
import service.{ProfileService, UserService}

@Singleton
class ProfileController @Inject()(
  cc: MessagesControllerComponents,
  profileService: ProfileService,
  userService: UserService,
  authenticatedUserAction: AuthenticatedUserAction,
  authenticatedUserMessagesAction: AuthenticatedUserMessagesAction
) extends MessagesAbstractController(cc) {

  private val logger = play.api.Logger(this.getClass)

  val editProfileForm: Form[EditProfileForm] = Form(
    mapping(
      "linkedIn" -> optional(text),
      "gitHub" -> optional(text),
      "website" -> optional(text),
      "email" -> optional(text)
        .verifying("email address is already in use", s => userService.checkValidEmail(s)),
      "name" -> optional(text)
        .verifying("full name must contain at least a first name and last name", s => userService.checkValidName(s))
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

  def showResumes(): Action[AnyContent] = authenticatedUserMessagesAction { implicit request: MessagesRequest[AnyContent] =>
    val userId = request.session.data(Global.SESSION_USER_ID)
    val resumes = profileService.getResumes(userId)
    Ok(views.html.authenticated.user.profile.resumes(resumes))
  }

  def uploadResume(): Action[MultipartFormData[Files.TemporaryFile]] = Action(parse.multipartFormData) { request =>
    val userId = request.session.data(Global.SESSION_USER_ID)
    request.body
      .file("resume")
      .map { resume =>
        profileService.addResume(userId, resume)

        Redirect(routes.ProfileController.showResumes())
          .withSession(request.session)
          .flashing("success" -> "Resume uploaded.")
      }
      .getOrElse {
        Redirect(routes.ProfileController.showResumes())
          .withSession(request.session)
          .flashing("error" -> "Error uploading resume.")
      }
  }

  def deleteResume(version: Int): Action[AnyContent] = authenticatedUserMessagesAction { implicit request: MessagesRequest[AnyContent] =>
    val userId = request.session.data(Global.SESSION_USER_ID)
    profileService.deleteResume(userId, version)
    Redirect(routes.ProfileController.showResumes())
  }


  def logout: Action[AnyContent] = authenticatedUserAction { implicit request =>
    Redirect(routes.UserController.showLoginForm())
      .flashing("info" -> "You are logged out.")
      .withNewSession
  }

}
