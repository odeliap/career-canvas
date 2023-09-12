package careercanvas.io.utils

import javax.inject.Singleton

@Singleton
class StringUtils {

  def removeLeadingNewlinesAndSpaces(input: String): String = {
    val pattern = """^\s+""".r
    pattern.replaceFirstIn(input, "")
  }

}
