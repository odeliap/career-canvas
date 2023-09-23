package service

import careercanvas.io.model.job._
import careercanvas.io.{JobApplicationFilesDao, UserDao}
import careercanvas.io.processor.JobResponseWriter
import careercanvas.io.storage.StorageService
import careercanvas.io.util.AwaitResult
import utils.StringUtils

import java.sql.Timestamp
import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.ExecutionContext

class DocumentsService @Inject() (
  jobApplicationFilesDao: JobApplicationFilesDao,
  userDao: UserDao,
  jobResponseWriter: JobResponseWriter,
  storageService: StorageService,
  stringUtils: StringUtils
)(implicit ec: ExecutionContext)
  extends AwaitResult {

  def getApplicationsFiles(userId: String, jobId: Long): Seq[ApplicationFileUrl] = {
    jobApplicationFilesDao
      .getFilesByJob(userId.toLong, jobId)
      .waitForResult
      .map { applicationFile =>
        ApplicationFileUrl(applicationFile, storageService.generateSignedUrl(applicationFile.bucket, applicationFile.prefix))
      }
  }

  def saveFile(userId: String, jobId: Long, name: String, content: String, applicationFileType: ApplicationFileType): Unit = {
    val bucket = "career-canvas-application-files"
    val sanitizedName = stringUtils.sanitizeName(name)
    val prefix = s"$userId/$jobId/$sanitizedName.pdf"
    storageService.saveStringToTempFileAndUpload(content, bucket, prefix)
    val applicationFile = ApplicationFile(
      userId.toLong,
      jobId,
      0L,
      name,
      applicationFileType,
      bucket,
      prefix,
      Timestamp.valueOf(LocalDateTime.now())
    )
    jobApplicationFilesDao
      .add(applicationFile)
      .waitForResult
  }

  def deleteResponse(userId: String, jobId: Long, fileId: Long): Unit = {
    jobApplicationFilesDao.delete(userId.toLong, jobId, fileId).waitForResult
  }

  def generateCoverLetter(jobInfo: JobInfo, resumeVersion: String): Response = {
    val userInfo = userDao.getUser(jobInfo.userId).waitForResult.get
    jobResponseWriter.generateCoverLetter(jobInfo, stringUtils.stringToOptionInt(resumeVersion), userInfo)
  }

  def generateResponse(jobInfo: JobInfo, resumeVersion: String, question: String): Response = {
    val userInfo = userDao.getUser(jobInfo.userId).waitForResult.get
    jobResponseWriter.generateResponse(jobInfo, userInfo, stringUtils.stringToOptionInt(resumeVersion), question)
  }

  def improveResponse(response: String, improvements: Seq[ResponseImprovement], customImprovement: String): Response = {
    jobResponseWriter.improveResponse(response, improvements, customImprovement)
  }

}