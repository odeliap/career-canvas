package service

import careercanvas.io.{JobApplicationFilesDao, JobApplicationsDao, JobMetadataDao}
import careercanvas.io.model.job._
import careercanvas.io.processor.{JobMetadataResolver, JobResponseWriter}
import careercanvas.io.storage.StorageService
import careercanvas.io.util.AwaitResult
import utils.StringUtils

import java.sql.Timestamp
import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.ExecutionContext

class JobApplicationsService @Inject() (
  jobApplicationsDao: JobApplicationsDao,
  jobApplicationFilesDao: JobApplicationFilesDao,
  jobMetadataDao: JobMetadataDao,
  jobMetadataResolver: JobMetadataResolver,
  jobResponseWriter: JobResponseWriter,
  storageService: StorageService,
  stringUtils: StringUtils
)(implicit ec: ExecutionContext)
  extends AwaitResult {

  def createJob(data: UserProvidedJobDetails, postUrl: String, userId: String): Unit = {
    val jobInfo = JobInfo(
      jobId = 0L,
      userId = userId.toLong,
      company = data.company,
      jobTitle = data.jobTitle,
      postUrl = postUrl,
      status = JobStatus.stringToEnum(data.status),
      appSubmissionDate = None,
      interviewRound = data.interviewRound,
      notes = data.notes
    )
    val jobId = jobApplicationsDao.addJob(jobInfo).waitForResult
    val jobMetadata = jobMetadataResolver.resolve(jobId, postUrl)
    jobMetadataDao.addJob(jobMetadata).waitForResult
  }

  def resolveJobMetadata(jobId: Long): JobMetadata = {
    jobMetadataDao.getJobById(jobId).waitForResult
  }

  def deleteJob(userId: String, jobId: Long): Unit = {
    jobApplicationsDao.removeJob(userId.toLong, jobId)
  }

  def starJob(userId: String, jobId: Long): Unit = {
    val starred = getJobById(userId, jobId).starred
    val updateJobInfo = UpdateJobInfo(userId.toLong, jobId, None, None, None, None, Option(!starred))
    jobApplicationsDao.updateJobStatus(updateJobInfo)
  }

  def getJobById(userId: String, jobId: Long): JobInfo = {
    jobApplicationsDao.getJobById(userId.toLong, jobId).waitForResult
  }

  def getJobs(userId: String, page: Int, sortKey: SortKey): JobsResult = {
    val limit = 1
    jobApplicationsDao.getJobs(userId.toLong, page, limit, sortKey).waitForResult
  }

  def getApplicationsFiles(userId: String, jobId: Long): Seq[ApplicationFile] = {
    jobApplicationFilesDao
      .getFilesByJob(userId.toLong, jobId)
      .waitForResult
  }

  def saveFile(userId: String, jobId: Long, name: String, content: String, applicationFileType: ApplicationFileType): Unit = {
    val bucket = "career-canvas-application-files"
    val sanitizedName = stringUtils.sanitizeName(name)
    val prefix = s"$userId/$jobId/$sanitizedName"
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

  def generateCoverLetter(jobInfo: JobInfo, name: String): Response = {
    jobResponseWriter.generateCoverLetter(jobInfo, name)
  }

  def generateResponse(jobInfo: JobInfo, name: String, question: String): Response = {
    jobResponseWriter.generateResponse(jobInfo, name, question)
  }

  def improveResponse(response: String, improvements: Seq[ResponseImprovement], customImprovement: String): Response = {
    jobResponseWriter.improveResponse(response, improvements, customImprovement)
  }

}
