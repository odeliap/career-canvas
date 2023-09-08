package careercanvas.io.storage

import java.io.{File, InputStream}

trait StorageService {

  def getInputStream(bucket: String, key: String): InputStream

  def uploadFile(bucket: String, key: String, file: File): Unit

  def deleteFile(bucket: String, key: String): Unit

  def generateSignedUrl(bucket: String, key: String): String

  def saveStringToTempFileAndUpload(content: String, bucketName: String, key: String): Unit
}
