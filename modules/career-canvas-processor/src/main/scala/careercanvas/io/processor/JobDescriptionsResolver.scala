package careercanvas.io.processor

import careercanvas.io.model.job.JobDescriptions
import careercanvas.io.scraper.Scraper
import careercanvas.io.util.AwaitResult

import javax.inject.{Inject, Singleton}

@Singleton
class JobDescriptionsResolver @Inject()(
  scraper: Scraper,
  openAiConnector: OpenAiConnector
) extends AwaitResult {

  def resolve(jobId: Long, pageUrl: String): JobDescriptions = {
    val content = scraper.getPageContent(pageUrl)
    val prompt = resolvePrompt(content)
    val completion = openAiConnector.complete(prompt)
    completionToJobDescriptions(jobId, completion)
  }

  private def completionToJobDescriptions(jobId: Long, completion: String): JobDescriptions = {
    val args = completion.split("//////")
    val jobDescription = args(0)
    val companyDescription = args(1)
    JobDescriptions(jobId, jobDescription, companyDescription)
  }

  private def resolvePrompt(content: String): String = {
    s"""Extract the job description and company description from the following job post,
       |formatting the result like: <job description>//////<company description>.
       |Keep each field to less than 1024 characters long.
       |Do not include anything other than this result in your response.
       |If you cannot resolve one of these fields, keep the above formatting and in place of the result
       |put the text "Unable to resolve description".
       |
       |Here is the job posting:
       |$content
    """.stripMargin
  }


}
