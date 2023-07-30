package odeliaputterman.com.jobhunter.dao.impl

import odeliaputterman.com.jobhunter.ConnectionsDao
import odeliaputterman.com.jobhunter.dao.components.NetworkComponent
import odeliaputterman.com.jobhunter.model.{ConnectionInfo, UpdateConnectionInfo}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/**
 * dao to interact with the database to access connections
 * information. All the validation for calling these methods
 * should happen downstream.
 */
class ConnectionsDaoImpl @Inject() (
  protected val dbConfigProvider: DatabaseConfigProvider
) (implicit ec: ExecutionContext)
  extends ConnectionsDao
  with NetworkComponent
  with HasDatabaseConfigProvider[JdbcProfile] {

  import odeliaputterman.com.jobhunter.dao.profile.JobHunterSlickProfile.api._

  override def addConnection(connectionInfo: ConnectionInfo): Future[Long] = {
    val insertQuery = NetworkQuery returning NetworkQuery.map(_.connectionId) += connectionInfo

    db.run(insertQuery)
  }

  override def updateConnection(updateConnectionInfo: UpdateConnectionInfo): Future[Unit] = {
    val connectionInfoQuery = NetworkQuery
      .filter(_.connectionId === updateConnectionInfo.connectionId)

    val tx = for {
      connectionInfo <- connectionInfoQuery.result.head

      _ <- connectionInfoQuery.update(connectionInfo.patch(updateConnectionInfo))
    } yield ()

    db.run(tx.transactionally)
  }

  override def removeConnection(userId: Long, connectionId: Long): Future[Unit] = {
    val removeConnectionQuery = NetworkQuery
      .filter(row => row.userId === userId &&row.connectionId === connectionId)
      .delete

    db.run(removeConnectionQuery).map(_ => true)
  }

}
