package careercanvas.io

import careercanvas.io.model.connections.{ConnectionInfo, UpdateConnectionInfo}

import scala.concurrent.Future

trait ConnectionsDao {

  def addConnection(connectionInfo: ConnectionInfo): Future[Long]

  def updateConnection(updateConnectionInfo: UpdateConnectionInfo): Future[Unit]

  def removeConnection(userId: Long, connectionId: Long): Future[Unit]

}
