package careercanvas.io.converter

import careercanvas.io.model.feed.SortKey
import play.api.data.FormError
import play.api.data.format.Formatter

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

  implicit def sortKeyFormatter: Formatter[SortKey] = new Formatter[SortKey] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], SortKey] = {
      data.get(key)
        .map(SortKey.stringToEnum)
        .toRight(Seq(FormError(key, "error.required", Nil)))
    }

    override def unbind(key: String, value: SortKey): Map[String, String] = {
      Map(key -> value.toString)
    }
  }

}
