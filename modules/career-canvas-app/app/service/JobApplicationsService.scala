package service

import careercanvas.io.JobApplicationsDao
import careercanvas.io.model.{JobInfo, JobStatus, UserProvidedJobDetails}
import careercanvas.io.util.AwaitResult

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class JobApplicationsService @Inject() (
  jobApplicationsDao: JobApplicationsDao
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

}
