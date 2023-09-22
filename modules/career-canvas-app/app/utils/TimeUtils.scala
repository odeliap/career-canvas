package utils

import java.sql.Timestamp
import java.text.SimpleDateFormat
import javax.inject.Singleton

@Singleton
object TimeUtils {

  def format(ts: Timestamp): String = {
    val sdf = new SimpleDateFormat("d MMMM, HH:mm")
    sdf.format(ts)
  }

}
