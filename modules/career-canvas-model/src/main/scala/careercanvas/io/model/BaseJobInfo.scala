package careercanvas.io.model

import play.api.mvc.QueryStringBindable

case class BaseJobInfo(
  company: String,
  jobTitle: String,
  postUrl: String
)

object BaseJobInfo {
  implicit def queryStringBinder(implicit stringBinder: QueryStringBindable[String]) = new QueryStringBindable[BaseJobInfo] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, BaseJobInfo]] = {
      for {
        company <- stringBinder.bind("company", params)
        jobTitle <- stringBinder.bind("jobTitle", params)
        postUrl <- stringBinder.bind("postUrl", params)
      } yield {
        (company, jobTitle, postUrl) match {
          case (Right(company), Right(jobTitle), Right(postUrl)) =>
            Right(BaseJobInfo(company, jobTitle, postUrl))
          case _ =>
            Left("Unable to bind a BaseJobInfo")
        }
      }
    }

    override def unbind(key: String, baseJobInfo: BaseJobInfo): String = {
      stringBinder.unbind("company", baseJobInfo.company) + "&" +
        stringBinder.unbind("jobTitle", baseJobInfo.jobTitle) + "&" +
        stringBinder.unbind("postUrl", baseJobInfo.postUrl)
    }
  }
}