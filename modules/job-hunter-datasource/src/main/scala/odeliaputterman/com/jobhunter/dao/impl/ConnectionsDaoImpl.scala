package odeliaputterman.com.jobhunter.dao.impl

import odeliaputterman.com.jobhunter.ConnectionsDao
import odeliaputterman.com.jobhunter.model.{ConnectionInfo, UpdateConnectionInfo}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ConnectionsDaoImpl @Inject() (
  protected val dbConfigProvider: DatabaseConfigProvider
) (implicit ec: ExecutionContext)
  extends ConnectionsDao
  with HasDatabaseConfigProvider[JdbcProfile] {

  import odeliaputterman.com.jobhunter.dao.profile.JobHunterSlickProfile.api._

  override def addConnection(connectionInfo: ConnectionInfo): Future[Unit] = ???

  override def updateConnection(updateConnectionInfo: UpdateConnectionInfo): Future[Unit] = ???

  override def removeConnection(userId: Long, connectionId: Long): Future[Unit] = ???

}
