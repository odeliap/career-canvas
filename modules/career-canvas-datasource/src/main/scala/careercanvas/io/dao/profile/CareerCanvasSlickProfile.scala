package careercanvas.io.dao.profile

import careercanvas.io.model.connections.ConnectionCloseness
import careercanvas.io.model.job.{ApplicationFileType, JobStatus}
import com.github.tminglei.slickpg._
import slick.jdbc.{JdbcType, PostgresProfile}

trait CareerCanvasSlickProfile
  extends PostgresProfile
  with PgHStoreSupport
  with PgEnumSupport
  with PgDate2Support {

  override val api: CustomPGAPI = new CustomPGAPI {}

  trait CustomPGAPI extends API with HStoreImplicits {

    implicit val jobStatusConverter: JdbcType[JobStatus] = {
      createEnumJdbcType[JobStatus](
        JobStatus.toString,
        _.toString,
        JobStatus.stringToEnum,
        quoteName = false
      )
    }

    implicit val connectionClosenessConverter: JdbcType[ConnectionCloseness] = {
      createEnumJdbcType[ConnectionCloseness](
        ConnectionCloseness.toString,
        _.toString,
        ConnectionCloseness.stringToEnum,
        quoteName = false
      )
    }

    implicit val applicationFileTypeConverter: JdbcType[ApplicationFileType] = {
      createEnumJdbcType[ApplicationFileType](
        ApplicationFileType.toString,
        _.toString,
        ApplicationFileType.stringToEnum,
        quoteName = false
      )
    }

  }
}

object CareerCanvasSlickProfile extends CareerCanvasSlickProfile