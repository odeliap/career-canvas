package utils

import careercanvas.io.model.job.JobStatus

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

object ColorUtils {
  def localDateTimeToColor(timestamp: LocalDateTime): String = {
    val now = LocalDateTime.now()
    val ageDays = ChronoUnit.DAYS.between(timestamp, now)

    val color = ageDays match {
      case d if d < 1 => "#BCD2E8"
      case d if d <= 7 => "#91BAD6"
      case d if d <= 14 => "#73A5C6"
      case d if d <= 21 => "#528AAE"
      case d if d <= 29 => "#2E5984"
      case _ => "#1E3F66"
    }
    color
  }

  def jobStatusToColor(status: JobStatus): String = {
    status match {
      case JobStatus.Bookmarked => "#2E5984"
      case JobStatus.Applying => "#528AAE"
      case JobStatus.Applied => "#73A5C6"
      case JobStatus.Interviewing => "#91BAD6"
      case JobStatus.Offer => "#BCD2E8"
      case JobStatus.Rejected => "#1E3F66"
    }
  }
}
