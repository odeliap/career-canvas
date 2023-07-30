package odeliaputterman.com.jobhunter.converter

import java.sql.Timestamp
import java.time.{OffsetDateTime, ZoneId}
import scala.language.implicitConversions

trait Converters {

  implicit def timestampToOffsetDateTime(timestamp: Timestamp): OffsetDateTime = {
    OffsetDateTime.ofInstant(timestamp.toInstant, ZoneId.systemDefault())
  }

  implicit def offsetDateTimeToTimestamp(offsetDateTime: OffsetDateTime): Timestamp = {
    Timestamp.from(offsetDateTime.toInstant)
  }

}
