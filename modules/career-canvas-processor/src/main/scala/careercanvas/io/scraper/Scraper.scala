package careercanvas.io.scraper

import org.jsoup.Jsoup

import javax.inject.{Inject, Singleton}
import scala.util.{Failure, Success, Try}

@Singleton
class Scraper @Inject()() {

  def getPageContent(pageUrl: String): String = {
    Try {
      Jsoup.connect(pageUrl).timeout(10000).get().body()
    } match {
      case Success(content) => content.toString.substring(0, 1800)
      case Failure(_) => ""
    }
  }

}