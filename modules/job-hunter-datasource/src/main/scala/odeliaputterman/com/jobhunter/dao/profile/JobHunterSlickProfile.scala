package odeliaputterman.com.jobhunter.dao.profile

import com.github.tminglei.slickpg._

trait JobHunterSlickProfile
  extends ExPostgresProfile
  with PgHStoreSupport {

  override val api: API = CustomPGAPI

  object CustomPGAPI extends API with HStoreImplicits
}

object JobHunterSlickProfile extends JobHunterSlickProfile