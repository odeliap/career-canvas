package service

import careercanvas.io._
import careercanvas.io.model.job._
import careercanvas.io.model.user.Resume
import careercanvas.io.processor.JobResponseWriter
import careercanvas.io.processor.resolvers.JobDescriptionsResolver
import careercanvas.io.storage.StorageService
import careercanvas.io.util.AwaitResult
import utils.StringUtils

import java.sql.Timestamp
import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.ExecutionContext

class JobApplicationsService @Inject() (
  jobApplicationsDao: JobApplicationsDao,
  jobDescriptionsDao: JobDescriptionsDao,
  resumeDao: ResumeDao,
  jobDescriptionsResolver: JobDescriptionsResolver,
)(implicit ec: ExecutionContext)
  extends AwaitResult {

  def createJob(data: UserProvidedJobDetails, postUrl: String, userId: String): Unit = {
    val jobDescriptions = jobDescriptionsResolver.resolve(postUrl).copy(notes = data.notes)
    val jobId = jobDescriptionsDao.addJob(jobDescriptions).waitForResult
    val jobInfo = JobInfo(
      jobId = jobId,
      userId = userId.toLong,
      postUrl = postUrl,
      company = data.company,
      jobTitle = data.jobTitle,
      jobType = JobType.stringToEnum(data.jobType),
      location = data.location,
      salaryRange = data.salaryRange,
      status = JobStatus.Bookmarked,
      appSubmissionDate = None,
    )
    jobApplicationsDao.addJob(jobInfo).waitForResult
  }

  def resolveJobDescriptions(jobId: Long): JobDescriptions = {
    jobDescriptionsDao.getJobById(jobId).waitForResult
  }

  def deleteJob(userId: String, jobId: Long): Unit = {
    jobApplicationsDao.removeJob(userId.toLong, jobId)
  }

  def starJob(userId: String, jobId: Long): Unit = {
    val starred = getJobById(userId, jobId).starred
    val updateJobInfo = UpdateJobInfo(userId.toLong, jobId, None, None, None, None, Option(!starred))
    jobApplicationsDao.updateJobStatus(updateJobInfo)
  }

  def updateStatus(userId: String, jobId: String, status: JobStatus): Unit = {
    val updateJobInfo = UpdateJobInfo(userId.toLong, jobId.toLong, Option(status), None, Option(Timestamp.valueOf(LocalDateTime.now())), None, None)
    jobApplicationsDao.updateJobStatus(updateJobInfo).waitForResult
  }

  def updateJobType(userId: String, jobId: String, jobType: JobType): Unit = {
    val updateJobInfo = UpdateJobInfo(userId.toLong, jobId.toLong, None, Option(jobType), Option(Timestamp.valueOf(LocalDateTime.now())), None, None)
    jobApplicationsDao.updateJobStatus(updateJobInfo).waitForResult
  }

  def updateNotes(jobId: Long, updatedNotes: String): Unit = {
    val updateJobInfo = UpdateJobDescriptions(jobId, None, None, None, Option(updatedNotes))
    jobDescriptionsDao.updateJob(updateJobInfo).waitForResult
  }

  def updateAbout(jobId: Long, updatedAbout: String): Unit = {
    val updateJobDescriptions = UpdateJobDescriptions(jobId, Option(updatedAbout), None, None, None)
    jobDescriptionsDao.updateJob(updateJobDescriptions)
  }

  def updateRequirements(jobId: Long, updatedRequirements: String): Unit = {
    val updateJobDescriptions = UpdateJobDescriptions(jobId, None, Option(updatedRequirements), None, None)
    jobDescriptionsDao.updateJob(updateJobDescriptions)
  }

  def updateTechStack(jobId: Long, updatedTechStack: String): Unit = {
    val updateJobDescriptions = UpdateJobDescriptions(jobId, None, None, Option(updatedTechStack), None)
    jobDescriptionsDao.updateJob(updateJobDescriptions)
  }

  def getJobById(userId: String, jobId: Long): JobInfo = {
    jobApplicationsDao.getJobById(userId.toLong, jobId).waitForResult
  }

  def getJobs(userId: String): Seq[JobInfo] = {
    jobApplicationsDao.getJobs(userId.toLong).waitForResult
  }

  def getResumes(userId: String): Seq[Resume] = {
    resumeDao.getAll(userId.toLong).waitForResult
  }

}
