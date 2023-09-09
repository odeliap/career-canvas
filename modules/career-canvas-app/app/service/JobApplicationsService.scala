package service

import careercanvas.io.{JobApplicationFilesDao, JobApplicationsDao}
import careercanvas.io.model.job._
import careercanvas.io.processor.CoverLetterWriter
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
  coverLetterWriter: CoverLetterWriter,
  storageService: StorageService,
  stringUtils: StringUtils
)(implicit ec: ExecutionContext)
  extends AwaitResult {

  def createJob(data: UserProvidedJobDetails, postUrl: String, userId: String): Long = {
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
    jobApplicationsDao.addJob(jobInfo).waitForResult
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

  def getJobs(userId: String, sortKey: SortKey, page: Int = 1): JobsResult = {
    val limit = 30
    val offset = (page - 1) * limit
    jobApplicationsDao.getJobs(userId.toLong, offset, limit, sortKey).waitForResult
  }

  def getCoverLetters(userId: String, jobId: Long): Seq[ApplicationFile] = {
    jobApplicationFilesDao
      .getFilesByJob(userId.toLong, jobId)
      .waitForResult
      .filter(_.fileType.equals(ApplicationFileType.CoverLetter))
  }

  def getResponses(userId: String, jobId: Long): Seq[ApplicationFile] = {
    jobApplicationFilesDao
      .getFilesByJob(userId.toLong, jobId)
      .waitForResult
      .filter(_.fileType.equals(ApplicationFileType.Response))
  }

  def saveCoverLetter(userId: String, jobId: Long, name: String, content: String): Unit = {
    val bucket = "career-canvas-application-files"
    val sanitizedName = stringUtils.sanitizeName(name)
    val prefix = s"$userId/$jobId/$sanitizedName"
    storageService.saveStringToTempFileAndUpload(content, bucket, prefix)
    val applicationFile = ApplicationFile(
      userId.toLong,
      jobId,
      0L,
      name,
      ApplicationFileType.CoverLetter,
      bucket,
      prefix,
      Timestamp.valueOf(LocalDateTime.now())
    )
    jobApplicationFilesDao
      .add(applicationFile)
      .waitForResult
  }

  def generateCoverLetter(jobInfo: JobInfo, name: String): CoverLetter = {
    coverLetterWriter.generateCoverLetter(jobInfo, name)
  }

}
