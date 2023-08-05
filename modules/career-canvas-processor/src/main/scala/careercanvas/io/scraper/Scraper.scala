package careercanvas.io.scraper

import careercanvas.io.model.PageTitle
import org.jsoup.Jsoup

import javax.inject.{Inject, Singleton}
import scala.util.{Failure, Success, Try}

@Singleton
class Scraper @Inject()() {

  def getPageTitle(pageUrl: String): PageTitle = {
    Try {
      Jsoup.connect(pageUrl).timeout(10000).get().title()
    } match {
      case Success(title) => PageTitle(title)
      case Failure(_) => PageTitle("")
    }
  }

  def getPageContent(pageUrl: String): String = {
    Try {
      Jsoup.connect(pageUrl).timeout(10000).get().body()
    } match {
      case Success(content) => content.toString.substring(0, 2049)
      case Failure(_) => ""
    }
  }

}