package controllers

import careercanvas.io.model.user.{BaseUser, User, UserPublicLoginInfo}
import model.Global
import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText}

import javax.inject._
import play.api.mvc._
import service.UserService
import utils.FormUtils

@Singleton
class LandingController @Inject()(
  cc: MessagesControllerComponents,
  formUtils: FormUtils,
  userService: UserService
) extends MessagesAbstractController(cc) {

  private val logger = play.api.Logger(this.getClass)

  val loginForm: Form[BaseUser] = Form (
    mapping(
      "username" -> nonEmptyText
        .verifying("too few chars",  s => formUtils.lengthIsGreaterThanNCharacters(s, 3))
        .verifying("too many chars", s => formUtils.lengthIsLessThanNCharacters(s, 320)),
      "password" -> nonEmptyText
        .verifying("too few chars",  s => formUtils.lengthIsGreaterThanNCharacters(s, 10))
        .verifying("too many chars", s => formUtils.lengthIsLessThanNCharacters(s, 30)),
    )(BaseUser.apply)(BaseUser.unapply)
  )

  val signUpForm: Form[User] = Form (
    mapping(
      "fullName" -> nonEmptyText,
      "username" -> nonEmptyText
        .verifying("too few chars",  s => formUtils.lengthIsGreaterThanNCharacters(s, 3))
        .verifying("too many chars", s => formUtils.lengthIsLessThanNCharacters(s, 320)),
      "password" -> nonEmptyText
        .verifying("too few chars",  s => formUtils.lengthIsGreaterThanNCharacters(s, 10))
        .verifying("too many chars", s => formUtils.lengthIsLessThanNCharacters(s, 30)),
    )(User.apply)(User.unapply)
  )

  val forgotPasswordForm: Form[UserPublicLoginInfo] = Form (
    mapping(
      "email" -> nonEmptyText
        .verifying("too many chars", s => formUtils.lengthIsLessThanNCharacters(s, 320))
    )(UserPublicLoginInfo.apply)(UserPublicLoginInfo.unapply)
  )

  private val loginSubmitUrl = routes.LandingController.processLoginAttempt()

  private val signUpUrl = routes.LandingController.processSignUpAttempt()

  private val forgotPasswordUrl = routes.LandingController.forgotPassword()

  def showWelcomePage(): Action[AnyContent] = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.landing.WelcomeLandingView(loginForm, loginSubmitUrl))
  }

  def showSignUpForm: Action[AnyContent] = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.user.UserSignUpView(signUpForm, signUpUrl))
  }

  def showLoginForm: Action[AnyContent] = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.user.UserLoginView(loginForm, loginSubmitUrl))
  }

  def processSignUpAttempt: Action[AnyContent] = Action { implicit request: MessagesRequest[AnyContent] =>
    val errorFunction = { formWithErrors: Form[User] =>
      // form validation/binding failed...
      BadRequest(views.html.user.UserSignUpView(formWithErrors, loginSubmitUrl))
    }
    val successFunction = { user: User =>
      userService.attemptUserCreation(user) match {
        case Some(userId) =>
          Redirect(routes.LandingController.showLoginForm())
            .withSession(Global.SESSION_USER_ID -> userId.toString, Global.SESSION_USERNAME_KEY -> user.email, Global.SESSION_USER_FULL_NAME -> user.fullName)
            .flashing("success" -> "Account created. Please log in.")
        case None =>
          Redirect(routes.LandingController.showSignUpForm())
            .flashing("error" -> "An account registered with this email already exists.")
      }
    }
    val formValidationResult: Form[User] = signUpForm.bindFromRequest
    formValidationResult.fold(
      errorFunction,
      successFunction
    )
  }

  def processLoginAttempt: Action[AnyContent] = Action { implicit request: MessagesRequest[AnyContent] =>
    val errorFunction = { formWithErrors: Form[BaseUser] =>
      // form validation/binding failed...
      BadRequest(views.html.user.UserLoginView(formWithErrors, loginSubmitUrl))
    }
    val successFunction = { user: BaseUser =>
      // form validation/binding succeeded ...
      userService.lookupUser(user) match {
        case Some(userId) =>
          val fullName = userService.getUserName(userId).get
          Redirect(routes.JobFeedController.showJobFeedHome())
            .withSession(Global.SESSION_USER_ID -> userId.toString, Global.SESSION_USERNAME_KEY -> user.email, Global.SESSION_USER_FULL_NAME -> fullName)
        case None =>
          Redirect(routes.LandingController.showLoginForm())
            .flashing("error" -> "Invalid username/password.")
      }
    }
    val formValidationResult: Form[BaseUser] = loginForm.bindFromRequest
    formValidationResult.fold(
      errorFunction,
      successFunction
    )
  }

  def forgotPassword: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.user.ForgotPasswordView(forgotPasswordForm, forgotPasswordUrl))
  }

  def doForgotPasswordProcessing(): Action[AnyContent] = Action { implicit request: MessagesRequest[AnyContent] =>
    val errorFunction = { formWithErrors: Form[UserPublicLoginInfo] =>
      BadRequest(views.html.user.ForgotPasswordView(formWithErrors, forgotPasswordUrl))
    }
    val successFunction = { userPublicLoginInfo: UserPublicLoginInfo =>
      // TODO: send reset password link to email
      Redirect(routes.LandingController.forgotPassword())
        .flashing("success" -> "Sent reset password link.")
    }
    val formValidationResult: Form[UserPublicLoginInfo] = forgotPasswordForm.bindFromRequest
    formValidationResult.fold(
      errorFunction,
      successFunction
    )
  }

}