package odeliaputterman.com.jobhunter

import odeliaputterman.com.jobhunter.model.{JobInfo, UpdateJobInfo}

trait JobApplicationsDao {

  def addJob(jobInfo: JobInfo): Long

  def updateJobStatus(updateJobInfo: UpdateJobInfo): Boolean

  def removeJob(userId: String, jobId: Long): Unit

}
