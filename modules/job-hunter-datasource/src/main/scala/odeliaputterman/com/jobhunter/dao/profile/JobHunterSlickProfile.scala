package odeliaputterman.com.jobhunter.dao.profile

import com.github.tminglei.slickpg._
import odeliaputterman.com.jobhunter.model.{ConnectionCloseness, JobStatus}
import slick.jdbc.JdbcType

trait JobHunterSlickProfile
  extends ExPostgresProfile
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

  }
}

object JobHunterSlickProfile extends JobHunterSlickProfile