package careercanvas.io.processor

import careercanvas.io.model.job.{BaseJobInfo, JobType}
import careercanvas.io.scraper.Scraper
import careercanvas.io.util.AwaitResult
import careercanvas.io.utils.StringUtils

import javax.inject.{Inject, Singleton}

@Singleton
class BaseJobInfoResolver @Inject()(
  scraper: Scraper,
  openAiConnector: OpenAiConnector,
  stringUtils: StringUtils
) extends AwaitResult {

  def resolve(pageUrl: String): BaseJobInfo = {
    val content = scraper.getPageContent(pageUrl)
    val prompt = resolvePrompt(content)
    val completion = openAiConnector.complete(prompt)
    completionToBaseJobInfo(completion, pageUrl)
  }

  private def completionToBaseJobInfo(completion: String, pageUrl: String): BaseJobInfo = {
    val args = completion.split("//////")
    val company = stringUtils.removeLeadingNewlinesAndSpaces(args(0))
    val jobTitle = args(1)
    val jobType = JobType.stringToEnum(args(2))
    val location = args(3)
    val salaryRange = args(4)
    BaseJobInfo(pageUrl, company, jobTitle, jobType, location, salaryRange)
  }

  private def resolvePrompt(content: String): String = {
    s"""Extract the company name, job title, job type, location, and salary range from the following job post.
       |Format the result like <company>//////<job-title>//////<job-type>//////<location>//////<salary-range>.
       |Make the job type one of: FullTime, Contract, PartTime, Internship, and Temporary.
       |and keep the other fields to less than 255 characters long each.
       |Do not include anything other than this result in your response.
       |If you cannot resolve one of these fields, keep the above formatting and in place of the result
       |put the text "Unable to resolve".
       |
       |Here is the job posting:
       |$content
    """.stripMargin
  }


}
