package careercanvas.io.model.jobfeed

import careercanvas.io.model.EnumEntry

sealed trait CoverLetterImprovement extends EnumEntry

object CoverLetterImprovement {
  case object Personalization extends CoverLetterImprovement
  case object PositionRelevance extends CoverLetterImprovement
  case object StructureAndFlow extends CoverLetterImprovement
  case object Conciseness extends CoverLetterImprovement
  case object ActiveVoice extends CoverLetterImprovement
  case object Quantification extends CoverLetterImprovement
  case object IndustryJargon extends CoverLetterImprovement
  case object AnecdotalEvidence extends CoverLetterImprovement
  case object AvoidingRedundancy extends CoverLetterImprovement
  case object CallToAction extends CoverLetterImprovement
  case object Formatting extends CoverLetterImprovement
  case object GrammarAndSpelling extends CoverLetterImprovement
  case object Tone extends CoverLetterImprovement
  case object Variation extends CoverLetterImprovement
  case object KeywordOptimization extends CoverLetterImprovement
  case object SoftSkillsHighlight extends CoverLetterImprovement
  case object FollowUpIntent extends CoverLetterImprovement
  case object Other extends CoverLetterImprovement

  val values = Seq(Personalization, PositionRelevance, StructureAndFlow, Conciseness, ActiveVoice, Quantification, IndustryJargon, AnecdotalEvidence, AvoidingRedundancy, CallToAction, Formatting, GrammarAndSpelling, Tone, Variation, KeywordOptimization, SoftSkillsHighlight, FollowUpIntent, Other)

  def stringToEnum(input: String): CoverLetterImprovement = {
    values
      .find(improvement => improvement.toString.equalsIgnoreCase(input))
      .getOrElse(throw new IllegalArgumentException("Input string does not match any CoverLetterImprovement enum"))
  }
}
