package service

import careercanvas.io.UserDao
import careercanvas.io.converter.Converters
import careercanvas.io.model.user.{UpdateUserInfo, UserInfo}
import careercanvas.io.util.AwaitResult
import model.forms.EditProfileForm

import javax.inject.Inject
import scala.concurrent.ExecutionContext

@javax.inject.Singleton
class ProfileService @Inject()(
  userDao: UserDao
)(implicit ec: ExecutionContext)
  extends AwaitResult
    with Converters {

  def getUserInfo(id: String): UserInfo = {
    userDao.getUser(id.toLong).waitForResult.getOrElse(throw new IllegalArgumentException(s"No user exists with user id $id"))
  }

  def updateUserProfile(userId: String, editProfileForm: EditProfileForm): Unit = {
    val updateUserInfo = UpdateUserInfo(
      id = userId.toLong,
      fullName = editProfileForm.name,
      resume = editProfileForm.resume,
      linkedIn = editProfileForm.linkedIn,
      gitHub = editProfileForm.gitHub,
      website = editProfileForm.website
    )
    userDao.update(updateUserInfo).waitForResult
  }

}
