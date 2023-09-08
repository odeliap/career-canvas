package careercanvas.io.storage

import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model._

import java.io.{File, InputStream}
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest

import java.nio.file.{Files, Paths}
import java.time.Duration

class S3StorageService(s3Client: S3Client, presigner: S3Presigner) extends StorageService {

  override def getInputStream(bucket: String, key: String): InputStream = {
    val getObjectRequest = GetObjectRequest.builder()
      .bucket(bucket)
      .key(key)
      .build()
    s3Client.getObject(getObjectRequest)
  }

  override def uploadFile(bucket: String, key: String, file: File): Unit = {
    val uploadObjectRequest = PutObjectRequest.builder()
      .bucket(bucket)
      .key(key)
      .build()
    s3Client.putObject(uploadObjectRequest, RequestBody.fromFile(file))
  }

  override def deleteFile(bucket: String, key: String): Unit = {
    val deleteObjectRequest = DeleteObjectRequest.builder()
      .bucket(bucket)
      .key(key)
      .build()
    s3Client.deleteObject(deleteObjectRequest)
  }

  override def generateSignedUrl(bucket: String, key: String): String = {
    val getObjectRequest = GetObjectRequest.builder()
      .bucket(bucket)
      .key(key)
      .build()
    val getObjectPresignRequest = GetObjectPresignRequest.builder()
      .signatureDuration(Duration.ofHours(1))
      .getObjectRequest(getObjectRequest)
      .build()
    val presignedGetObjectRequest = presigner.presignGetObject(getObjectPresignRequest)
    val presignedUrl = presignedGetObjectRequest.url()
    presignedUrl.toString
  }

  override def saveStringToTempFileAndUpload(content: String, bucketName: String, key: String): Unit = {
    val tempFile = Files.createTempFile("prefix", ".txt")
    Files.write(tempFile, content.getBytes)

    try {
      s3Client.putObject(
        PutObjectRequest.builder().bucket(bucketName).key(key).build(),
        tempFile
      )
    } finally {
      Files.deleteIfExists(tempFile)
    }
  }

}
