package careercanvas.io.module

import careercanvas.io.storage.{S3StorageService, StorageService}
import com.google.inject.{AbstractModule, Provides}
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

class CareerCanvasAppModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[StorageService]).toInstance(s3StorageService())
  }

  @Provides
  def s3StorageService(): S3StorageService = {
    val region = Region.US_WEST_1
    val s3Client = S3Client.builder()
      .region(region)
      .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
      .build()

    new S3StorageService(s3Client)
  }

}
