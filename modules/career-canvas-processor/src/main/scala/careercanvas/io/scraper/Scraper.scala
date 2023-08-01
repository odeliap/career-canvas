package careercanvas.io.scraper

import careercanvas.io.model.PageTitle
import org.jsoup.Jsoup

import javax.inject.{Inject, Singleton}

@Singleton
class Scraper @Inject()() {

  def getPageTitle(pageUrl: String): PageTitle = {
    val connection = Jsoup.connect(pageUrl)
    connection.timeout(10000)
    val document = connection.get()
    PageTitle(document.title())
  }

}