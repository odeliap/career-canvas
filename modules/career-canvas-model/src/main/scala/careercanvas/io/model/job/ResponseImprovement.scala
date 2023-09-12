package careercanvas.io.model.job

import careercanvas.io.model.EnumEntry
import play.api.libs.json.{JsError, JsSuccess, Reads}

sealed trait ResponseImprovement extends EnumEntry

object ResponseImprovement {
  case object Personalize extends ResponseImprovement
  case object StructureAndFlow extends ResponseImprovement
  case object Conciseness extends ResponseImprovement
  case object ActiveVoice extends ResponseImprovement
  case object Quantification extends ResponseImprovement
  case object IndustryJargon extends ResponseImprovement
  case object AnecdotalEvidence extends ResponseImprovement
  case object AvoidingRedundancy extends ResponseImprovement
  case object CallToAction extends ResponseImprovement
  case object Formatting extends ResponseImprovement
  case object GrammarAndSpelling extends ResponseImprovement
  case object Tone extends ResponseImprovement
  case object Variation extends ResponseImprovement
  case object KeywordOptimization extends ResponseImprovement
  case object SoftSkillsHighlight extends ResponseImprovement

  val values = Seq(Personalize, StructureAndFlow, Conciseness, ActiveVoice, Quantification, IndustryJargon, AnecdotalEvidence, AvoidingRedundancy, CallToAction, Formatting, GrammarAndSpelling, Tone, Variation, KeywordOptimization, SoftSkillsHighlight)

  implicit val responseImprovementReads: Reads[ResponseImprovement] = Reads { json =>
    json.validate[String].flatMap {
      case "Personalize" => JsSuccess(Personalize)
      case "StructureAndFlow" => JsSuccess(StructureAndFlow)
      case "Conciseness" => JsSuccess(Conciseness)
      case "ActiveVoice" => JsSuccess(ActiveVoice)
      case "Quantification" => JsSuccess(Quantification)
      case "IndustryJargon" => JsSuccess(IndustryJargon)
      case "AnecdotalEvidence" => JsSuccess(AnecdotalEvidence)
      case "AvoidingRedundancy" => JsSuccess(AvoidingRedundancy)
      case "CallToAction" => JsSuccess(CallToAction)
      case "Formatting" => JsSuccess(Formatting)
      case "GrammarAndSpelling" => JsSuccess(GrammarAndSpelling)
      case "Tone" => JsSuccess(Tone)
      case "Variation" => JsSuccess(Variation)
      case "KeywordOptimization" => JsSuccess(KeywordOptimization)
      case "SoftSkillsHighlight" => JsSuccess(SoftSkillsHighlight)
      case _ => JsError("Unknown ResponseImprovement")
    }
  }

  def stringToEnum(input: String): ResponseImprovement = {
    values
      .find(improvement => improvement.toString.equalsIgnoreCase(input))
      .getOrElse(throw new IllegalArgumentException("Input string does not match any CoverLetterImprovement enum"))
  }

}
