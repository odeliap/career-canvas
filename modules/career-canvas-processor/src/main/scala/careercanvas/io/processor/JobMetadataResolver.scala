package careercanvas.io.processor

import careercanvas.io.model.job.JobMetadata
import careercanvas.io.scraper.Scraper
import careercanvas.io.util.AwaitResult

import javax.inject.{Inject, Singleton}

@Singleton
class JobMetadataResolver @Inject()(
  scraper: Scraper,
  openAiConnector: OpenAiConnector
) extends AwaitResult {

  def resolve(pageUrl: String): JobMetadata = {
    val content = scraper.getPageContent(pageUrl)
    val prompt = resolvePrompt(content)
    val completion = openAiConnector.complete(prompt)
    completionToJobMetadata(completion)
  }

  private def completionToJobMetadata(completion: String): JobMetadata = {
    val args = completion.split("//////")
    val location = args(0)
    val salary = args(1)
    val jobDescription = args(2)
    val companyDescription = args(3)
    JobMetadata(location, salary, jobDescription, companyDescription)
  }

  private def resolvePrompt(content: String): String = {
    s"""Extract the job location, annual salary, job description, and company description from the following job post,
       |formatting the result like <location>//////<annual salary>//////<job description>//////<company description> without any extra information:
       |$content
    """.stripMargin
  }


}
