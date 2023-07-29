package odeliaputterman.com.jobhunter

import odeliaputterman.com.jobhunter.model.{ConnectionInfo, UpdateConnectionInfo}

import scala.concurrent.Future

trait ConnectionsDao {

  def addConnection(connectionInfo: ConnectionInfo): Future[Unit]

  def updateConnection(updateConnectionInfo: UpdateConnectionInfo): Future[Unit]

  def removeConnection(userId: Long, connectionId: Long): Future[Unit]

}
