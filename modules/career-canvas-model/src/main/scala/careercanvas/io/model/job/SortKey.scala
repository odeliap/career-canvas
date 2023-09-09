package careercanvas.io.model.job

import careercanvas.io.model.EnumEntry

sealed trait SortKey extends EnumEntry

object SortKey {
  case object Company extends SortKey
  case object JobTitle extends SortKey
  case object Status extends SortKey
  case object ApplicationSubmissionDate extends SortKey
  case object LastUpdate extends SortKey

  val values = Seq(Company, JobTitle, Status, ApplicationSubmissionDate, LastUpdate)

  def stringToEnum(input: String): SortKey = {
    values
      .find(sortKey => sortKey.toString.equalsIgnoreCase(input))
      .getOrElse(throw new IllegalArgumentException("Input string does not match any SortKey enum"))
  }
}