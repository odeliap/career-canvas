package careercanvas.io.processor

import careercanvas.io.model.job.BaseJobInfo
import careercanvas.io.scraper.Scraper
import careercanvas.io.util.AwaitResult

import javax.inject.{Inject, Singleton}

@Singleton
class BaseJobInfoResolver @Inject()(
  scraper: Scraper,
  openAiConnector: OpenAiConnector
) extends AwaitResult {

  def resolve(pageUrl: String): BaseJobInfo = {
    val title = scraper.getPageTitle(pageUrl)
    val prompt = resolvePrompt(title.title)
    val completion = openAiConnector.complete(prompt)
    completionToBaseJobInfo(completion, pageUrl)
  }

  private def completionToBaseJobInfo(completion: String, pageUrl: String): BaseJobInfo = {
    val args = completion.split("//////")
    val company = args(0)
    val jobTitle = args(1)
    BaseJobInfo(company, jobTitle, pageUrl)
  }

  private def resolvePrompt(title: String): String = {
    s"""Extract the company name and job title from the following job post title,
       |formatting the result like <company>//////<job-title>:
       |$title
    """.stripMargin
  }


}
