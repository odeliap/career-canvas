package careercanvas.io.inject

import careercanvas.io._
import careercanvas.io.dao.impl._
import com.google.inject.{AbstractModule, Scopes}

import java.util.TimeZone

class SlickDaoModule() extends AbstractModule {

  TimeZone.setDefault(TimeZone.getTimeZone("UTC"))

  override def configure(): Unit = {
    bind(classOf[UserDao]).to(classOf[UserDaoImpl]).in(Scopes.SINGLETON)
    bind(classOf[JobApplicationsDao]).to(classOf[JobApplicationsDaoImpl]).in(Scopes.SINGLETON)
    bind(classOf[JobMetadataDao]).to(classOf[JobMetadataDaoImpl]).in(Scopes.SINGLETON)
    bind(classOf[JobStatisticsDao]).to(classOf[JobStatisticsDaoImpl]).in(Scopes.SINGLETON)
    bind(classOf[ResumeDao]).to(classOf[ResumeDaoImpl]).in(Scopes.SINGLETON)
    bind(classOf[JobApplicationFilesDao]).to(classOf[JobApplicationFilesDaoImpl]).in(Scopes.SINGLETON)
    bind(classOf[UserResetCodesDao]).to(classOf[UserResetCodesDaoImpl]).in(Scopes.SINGLETON)
  }

}
