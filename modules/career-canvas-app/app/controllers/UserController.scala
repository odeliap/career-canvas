package controllers

import careercanvas.io.model.User

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

  val form: Form[User] = Form (
    mapping(
      "username" -> nonEmptyText
        .verifying("too few chars",  s => lengthIsGreaterThanNCharacters(s, 3))
        .verifying("too many chars", s => lengthIsLessThanNCharacters(s, 320)),
      "password" -> nonEmptyText
        .verifying("too few chars",  s => lengthIsGreaterThanNCharacters(s, 10))
        .verifying("too many chars", s => lengthIsLessThanNCharacters(s, 30)),
    )(User.apply)(User.unapply)
  )

  private val formSubmitUrl = routes.UserController.processLoginAttempt

  private val signUpUrl = routes.UserController.processSignUpAttempt

  def showSignUpForm = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.user.signUp(form, signUpUrl))
  }

  def showLoginForm = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.user.userLogin(form, formSubmitUrl))
  }

  def processSignUpAttempt = Action { implicit request: MessagesRequest[AnyContent] =>
    val errorFunction = { formWithErrors: Form[User] =>
      // form validation/binding failed...
      BadRequest(views.html.user.signUp(formWithErrors, formSubmitUrl))
    }
    val successFunction = { user: User =>
      userService.attemptUserCreation(user) match {
        case Some(userId) =>
          Redirect(routes.UserController.showLoginForm)
            .withSession(Global.SESSION_USER_ID -> userId.toString, Global.SESSION_USERNAME_KEY -> user.email)
            .flashing("success" -> "Account created. Please log in.")
        case None =>
          Redirect(routes.UserController.showSignUpForm)
            .flashing("error" -> "An account registered with this email already exists.")
      }
    }
    val formValidationResult: Form[User] = form.bindFromRequest
    formValidationResult.fold(
      errorFunction,
      successFunction
    )
  }

  def processLoginAttempt = Action { implicit request: MessagesRequest[AnyContent] =>
    val errorFunction = { formWithErrors: Form[User] =>
      // form validation/binding failed...
      BadRequest(views.html.user.userLogin(formWithErrors, formSubmitUrl))
    }
    val successFunction = { user: User =>
      // form validation/binding succeeded ...
      userService.lookupUser(user) match {
        case Some(userId) =>
          Redirect(routes.AuthenticatedUserController.showHome)
            .withSession(Global.SESSION_USER_ID -> userId.toString, Global.SESSION_USERNAME_KEY -> user.email)
        case None =>
          Redirect(routes.UserController.showLoginForm)
            .flashing("error" -> "Invalid username/password.")
      }
    }
    val formValidationResult: Form[User] = form.bindFromRequest
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