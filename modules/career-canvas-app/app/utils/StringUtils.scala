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

}
