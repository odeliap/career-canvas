package service

import careercanvas.io.UserDao
import careercanvas.io.converter.Converters
import careercanvas.io.model.{BaseUser, UpdateUserInfo, User, UserShowcase}
import careercanvas.io.util.AwaitResult

import java.time.OffsetDateTime
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

@javax.inject.Singleton
class ProfileService @Inject()(
  userDao: UserDao
)(implicit ec: ExecutionContext)
  extends AwaitResult
    with Converters {

  def getUserShowcase(id: String): UserShowcase = {
    UserShowcase(id, Option("resume url"), Option("linkedIn url"), Option("github url"), Option("website url"))
  }

}
