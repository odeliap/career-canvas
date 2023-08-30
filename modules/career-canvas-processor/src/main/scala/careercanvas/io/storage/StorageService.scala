package careercanvas.io.storage

import java.io.{File, InputStream}

trait StorageService {

  def getInputStream(bucket: String, key: String): InputStream

  def uploadFile(bucket: String, key: String, file: File): Unit

}
