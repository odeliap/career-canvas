package service

import odeliaputterman.com.jobhunter.UserDao
import odeliaputterman.com.jobhunter.model.User
import odeliaputterman.com.jobhunter.util.AwaitResult

import javax.inject.Inject

@javax.inject.Singleton
class UserService @Inject()(
  userDao: UserDao
) extends AwaitResult {

  def lookupUser(user: User): Boolean = {
    userDao.checkUserInfo(user).waitForResult
  }

  def createUser(user: User): Boolean = {
    val exists = userDao.checkUserExists(user.email).waitForResult
    if (exists) {
      false
    } else {
      userDao.addUser(user).waitForResult
      true
    }
  }

}
