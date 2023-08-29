package careercanvas.io.model.jobfeed

import java.sql.Timestamp

case class UpdateJobInfo(
                          userId: Long,
                          jobId: Long,
                          status: Option[JobStatus],
                          appSubmissionDate: Option[Timestamp],
                          interviewRound: Option[Int],
                          notes: Option[String]
                        )
