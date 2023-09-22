package utils

import javax.inject.Singleton

@Singleton
class StringUtils {

  def sanitizeName(name: String): String = {
    name
      .filter(c => c.isLetterOrDigit || c == '_')
      .toLowerCase
      .replace('_', '-')
  }

  def stringToOptionInt(s: String): Option[Int] = {
    try {
      Some(s.toInt)
    } catch {
      case _: NumberFormatException => None
    }
  }

}
