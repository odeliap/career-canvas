package careercanvas.io.module

import careercanvas.io.email.{EmailService, SendGridEmailService}
import careercanvas.io.storage.{S3StorageService, StorageService}
import com.google.inject.{AbstractModule, Provides}
import com.typesafe.config.{Config, ConfigFactory}
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.presigner.S3Presigner

class CareerCanvasAppModule extends AbstractModule {

  val config: Config = ConfigFactory
    .load()
    .resolve()

  override def configure(): Unit = {
    bind(classOf[StorageService]).toInstance(s3StorageService())
    bind(classOf[EmailService]).toInstance(sendGridEmailService())
  }

  @Provides
  def s3StorageService(): S3StorageService = {
    val region = Region.US_WEST_1
    val credentialsProvider = EnvironmentVariableCredentialsProvider.create()
    val s3Client = S3Client.builder()
      .region(region)
      .credentialsProvider(credentialsProvider)
      .build()
    val presigner = S3Presigner.builder()
      .region(region)
      .credentialsProvider(credentialsProvider)
      .build()

    new S3StorageService(s3Client, presigner)
  }

  @Provides
  def sendGridEmailService(): SendGridEmailService = {
    val email = config.getString("sendgrid.email")
    val apiKey = config.getString("sendgrid.api.key")

    new SendGridEmailService(email, apiKey)
  }

}
