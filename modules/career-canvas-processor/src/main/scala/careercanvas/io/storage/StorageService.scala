package careercanvas.io.storage

import java.io.InputStream

trait StorageService {

  def getInputStream(bucket: String, key: String): InputStream

  def uploadFile(bucket: String, key: String, filePath: String): Unit

}
