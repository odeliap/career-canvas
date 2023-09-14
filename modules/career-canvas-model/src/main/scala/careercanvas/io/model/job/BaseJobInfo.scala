package careercanvas.io.model.job

import play.api.mvc.QueryStringBindable

case class BaseJobInfo(
  postUrl: String,
  company: String,
  jobTitle: String,
  jobType: JobType,
  location: String,
  salaryRange: String
) extends CompletionResolvedInfo

object BaseJobInfo {

  implicit def queryStringBinder(implicit stringBinder: QueryStringBindable[String]): QueryStringBindable[BaseJobInfo] = new QueryStringBindable[BaseJobInfo] {

    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, BaseJobInfo]] = {
      for {
        postUrl <- stringBinder.bind("postUrl", params)
        company <- stringBinder.bind("company", params)
        jobTitle <- stringBinder.bind("jobTitle", params)
        jobType <- stringBinder.bind("jobType", params)
        location <- stringBinder.bind("location", params)
        salaryRange <- stringBinder.bind("salaryRange", params)
      } yield {
        (postUrl, company, jobTitle, jobType, location, salaryRange) match {
          case (Right(postUrl), Right(company), Right(jobTitle), Right(jobType), Right(location), Right(salaryRange)) =>
            Right(BaseJobInfo(postUrl, company, jobTitle, JobType.stringToEnum(jobType), location, salaryRange))
          case _ =>
            Left("Unable to bind a BaseJobInfo")
        }
      }
    }

    override def unbind(key: String, baseJobInfo: BaseJobInfo): String = {
      stringBinder.unbind("postUrl", baseJobInfo.postUrl) + "&" +
        stringBinder.unbind("company", baseJobInfo.company) + "&" +
        stringBinder.unbind("jobTitle", baseJobInfo.jobTitle) + "&" +
        stringBinder.unbind("jobType", baseJobInfo.jobType.toString) + "&" +
        stringBinder.unbind("location", baseJobInfo.location) + "&" +
        stringBinder.unbind("salaryRange", baseJobInfo.salaryRange)
    }
  }

}