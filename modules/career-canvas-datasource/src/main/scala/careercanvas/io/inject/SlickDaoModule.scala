package careercanvas.io.inject

import careercanvas.io.{ConnectionsDao, JobApplicationsDao, UserDao}
import careercanvas.io.dao.impl.{ConnectionsDaoImpl, JobApplicationsDaoImpl, UserDaoImpl}
import com.google.inject.{AbstractModule, Scopes}

import java.util.TimeZone

class SlickDaoModule() extends AbstractModule {

  TimeZone.setDefault(TimeZone.getTimeZone("UTC"))

  override def configure(): Unit = {
    bind(classOf[UserDao]).to(classOf[UserDaoImpl]).in(Scopes.SINGLETON)
    bind(classOf[JobApplicationsDao]).to(classOf[JobApplicationsDaoImpl]).in(Scopes.SINGLETON)
    bind(classOf[ConnectionsDao]).to(classOf[ConnectionsDaoImpl]).in(Scopes.SINGLETON)
  }

}
