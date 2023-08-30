package careercanvas.io.storage

import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.{GetObjectRequest, PutObjectRequest}

import java.io.{File, InputStream}
import software.amazon.awssdk.core.sync.RequestBody

class S3StorageService(s3Client: S3Client) extends StorageService {

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

}
