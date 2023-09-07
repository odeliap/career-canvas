package service

import careercanvas.io.{JobApplicationFilesDao, JobApplicationsDao}
import careercanvas.io.model.job._
import careercanvas.io.processor.CoverLetterWriter
import careercanvas.io.util.AwaitResult

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class JobApplicationsService @Inject() (
  jobApplicationsDao: JobApplicationsDao,
  jobApplicationFilesDao: JobApplicationFilesDao,
  coverLetterWriter: CoverLetterWriter
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

  def getJobs(userId: String): Seq[JobInfo] = {
    jobApplicationsDao.getJobs(userId.toLong).waitForResult
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

  def generateCoverLetter(jobInfo: JobInfo, name: String): CoverLetter = {
    coverLetterWriter.generateCoverLetter(jobInfo, name)
  }

}
