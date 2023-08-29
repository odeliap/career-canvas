package controllers

import careercanvas.io.model.user.{BaseUser, User}
import model.Global
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import service.UserService

import javax.inject.Inject

class UserController @Inject()(
  cc: MessagesControllerComponents,
  userService: UserService
) extends MessagesAbstractController(cc) {

  private val logger = play.api.Logger(this.getClass)

  val loginForm: Form[BaseUser] = Form (
    mapping(
      "username" -> nonEmptyText
        .verifying("too few chars",  s => lengthIsGreaterThanNCharacters(s, 3))
        .verifying("too many chars", s => lengthIsLessThanNCharacters(s, 320)),
      "password" -> nonEmptyText
        .verifying("too few chars",  s => lengthIsGreaterThanNCharacters(s, 10))
        .verifying("too many chars", s => lengthIsLessThanNCharacters(s, 30)),
    )(BaseUser.apply)(BaseUser.unapply)
  )

  val signUpForm: Form[User] = Form (
    mapping(
      "fullName" -> nonEmptyText,
      "username" -> nonEmptyText
        .verifying("too few chars",  s => lengthIsGreaterThanNCharacters(s, 3))
        .verifying("too many chars", s => lengthIsLessThanNCharacters(s, 320)),
      "password" -> nonEmptyText
        .verifying("too few chars",  s => lengthIsGreaterThanNCharacters(s, 10))
        .verifying("too many chars", s => lengthIsLessThanNCharacters(s, 30)),
    )(User.apply)(User.unapply)
  )

  private val formSubmitUrl = routes.UserController.processLoginAttempt()

  private val signUpUrl = routes.UserController.processSignUpAttempt()

  def showSignUpForm: Action[AnyContent] = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.user.signUp(signUpForm, signUpUrl))
  }

  def showLoginForm: Action[AnyContent] = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.user.userLogin(loginForm, formSubmitUrl))
  }

  def processSignUpAttempt: Action[AnyContent] = Action { implicit request: MessagesRequest[AnyContent] =>
    val errorFunction = { formWithErrors: Form[User] =>
      // form validation/binding failed...
      BadRequest(views.html.user.signUp(formWithErrors, formSubmitUrl))
    }
    val successFunction = { user: User =>
      userService.attemptUserCreation(user) match {
        case Some(userId) =>
          Redirect(routes.UserController.showLoginForm())
            .withSession(Global.SESSION_USER_ID -> userId.toString, Global.SESSION_USERNAME_KEY -> user.email, Global.SESSION_USER_FULL_NAME -> user.fullName)
            .flashing("success" -> "Account created. Please log in.")
        case None =>
          Redirect(routes.UserController.showSignUpForm())
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
      BadRequest(views.html.user.userLogin(formWithErrors, formSubmitUrl))
    }
    val successFunction = { user: BaseUser =>
      // form validation/binding succeeded ...
      userService.lookupUser(user) match {
        case Some(userId) =>
          val fullName = userService.getUserName(userId).get
          Redirect(routes.HomeController.showHome())
            .withSession(Global.SESSION_USER_ID -> userId.toString, Global.SESSION_USERNAME_KEY -> user.email, Global.SESSION_USER_FULL_NAME -> fullName)
        case None =>
          Redirect(routes.UserController.showLoginForm())
            .flashing("error" -> "Invalid username/password.")
      }
    }
    val formValidationResult: Form[BaseUser] = loginForm.bindFromRequest
    formValidationResult.fold(
      errorFunction,
      successFunction
    )
  }

  private def lengthIsGreaterThanNCharacters(s: String, n: Int): Boolean = {
    if (s.length > n) true else false
  }

  private def lengthIsLessThanNCharacters(s: String, n: Int): Boolean = {
    if (s.length < n) true else false
  }

}