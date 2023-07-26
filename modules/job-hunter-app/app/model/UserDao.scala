package model

import javax.inject.Inject

@javax.inject.Singleton
class UserDao @Inject()() {

  def lookupUser(u: User): Boolean = {
    // TODO: query database here
    if (u.username == "foo" && u.password == "foo") true else false
  }

  def createUser(u: User): Boolean = {
    // TODO: add new user to database here
    true
  }

}
