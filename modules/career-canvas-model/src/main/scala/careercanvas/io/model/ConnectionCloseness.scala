package careercanvas.io.model

sealed trait ConnectionCloseness extends EnumEntry

object ConnectionCloseness {
  case object Stranger extends ConnectionCloseness
  case object Acquaintance extends ConnectionCloseness
  case object Friend extends ConnectionCloseness
  case object CloseFriend extends ConnectionCloseness
  case object FamilyMember extends ConnectionCloseness

  val values = Seq(Stranger, Acquaintance, Friend, CloseFriend, FamilyMember)

  def stringToEnum(input: String): ConnectionCloseness = {
    values
      .find(status => status.toString.equalsIgnoreCase(input))
      .getOrElse(throw new IllegalArgumentException("Input string does not match any ConnectionCloseness enum"))
  }
}