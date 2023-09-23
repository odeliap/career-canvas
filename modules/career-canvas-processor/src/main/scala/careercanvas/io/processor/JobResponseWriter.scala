package careercanvas.io.processor

import akka.actor.ActorSystem
import akka.stream.Materializer
import careercanvas.io.ResumeDao
import careercanvas.io.model.job.{JobInfo, Response, ResponseImprovement}
import careercanvas.io.model.user.UserInfo
import careercanvas.io.scraper.Scraper
import careercanvas.io.storage.StorageService
import careercanvas.io.util.AwaitResult
import io.cequence.openaiscala.service.{OpenAIService, OpenAIServiceFactory}
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class JobResponseWriter @Inject()(
  scraper: Scraper,
  openAiConnector: OpenAiConnector,
  storageService: StorageService,
  resumeDao: ResumeDao
)(implicit ec: ExecutionContext) extends AwaitResult  {

  implicit val materializer: Materializer = Materializer(ActorSystem())

  val openAiService: OpenAIService = OpenAIServiceFactory()

  def generateCoverLetter(jobInfo: JobInfo, resumeVersion: Option[Int], userInfo: UserInfo): Response = {
    val task = "Generate a cover letter."
    generateResponse(jobInfo, userInfo, resumeVersion, task)
  }

  def improveResponse(coverLetter: String, improvements: Seq[ResponseImprovement], customImprovement: String): Response = {
    val improvementsPrompt = resolveImprovements(improvements, customImprovement)
    val fullPrompt = resolveResponseImprovementPrompt(coverLetter, improvementsPrompt)
    promptToResponse(fullPrompt)
  }

  def generateResponse(jobInfo: JobInfo, userInfo: UserInfo, resumeVersion: Option[Int], task: String): Response = {
    val resumeContentsOpt = resumeVersion.map( version => getResumeContents(jobInfo.userId, version))
    val jobPostContent = scraper.getPageContent(jobInfo.postUrl)
    val fullPrompt = resolveFullPrompt(userInfo, jobInfo.company, jobInfo.jobTitle, jobPostContent, resumeContentsOpt, task)
    promptToResponse(fullPrompt)
  }

  private def getResumeContents(userId: Long, resumeVersion: Int): String = {
    val resume = resumeDao.getByVersion(userId, resumeVersion).waitForResult
    val inputStream = storageService.getInputStream(resume.bucket, resume.prefix)
    val document = PDDocument.load(inputStream)
    try {
      val stripper = new PDFTextStripper()
      stripper.getText(document)
    } finally {
      document.close()
    }
  }

  private def promptToResponse(fullPrompt: String): Response = {
    val completion = openAiConnector.complete(fullPrompt)
    Response(completion)
  }

  private def resolveImprovements(improvements: Seq[ResponseImprovement], customImprovement: String): String = {
    val providedImprovementsPrompt = if (improvements.nonEmpty) s"\nMake improvements to the following areas: ${improvements.mkString(", ")}" else ""
    val customImprovementPrompt = if (customImprovement.nonEmpty) s"\nMake the following improvement: $customImprovement" else ""
    providedImprovementsPrompt + customImprovementPrompt
  }

  private def resolveFullPrompt(userInfo: UserInfo, company: String, jobTitle: String, content: String, resume: Option[String], task: String): String = {
    s"""Given the following applicant and position details:
       |Applicant Name: ${userInfo.fullName}
       |Applicant Email: ${userInfo.email}
       |${resolveApplicantLinks(userInfo)}
       |
       |Job Title: $jobTitle
       |Company: $company
       |Job Description: $content
       |
       |Complete the following task: $task
       |
       |${resolveResumePrompt(resume)}
       |
       |Please format the response with newline characters suitable for Scala code.
    """.stripMargin
  }

  private def resolveResumePrompt(resume: Option[String]): String = {
    resume.map(r => s"Tailor the content to the following applicant resume: $r").getOrElse("")
  }

  private def resolveApplicantLinks(userInfo: UserInfo): String = {
    val linksMap = Map(
      "LinkedIn" -> userInfo.linkedIn,
      "GitHub" -> userInfo.gitHub,
      "Website" -> userInfo.website
    )

    linksMap.collect {
      case (platform, Some(link)) if link.trim.nonEmpty => s"$platform: $link"
    }.mkString("\n\n")
  }

  private def resolveResponseImprovementPrompt(coverLetter: String, improvementPrompts: String): String = {
    s"""
       |Improve the following response:
       |
       |$coverLetter
       |
       |Please do so by doing the following:
       |${improvementPrompts}
       |""".stripMargin
  }

}
