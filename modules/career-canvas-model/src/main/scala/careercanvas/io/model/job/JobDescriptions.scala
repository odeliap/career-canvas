package careercanvas.io.model.job

case class JobDescriptions(
  jobId: Long,
  about: String,
  requirements: String,
  techStack: String
) extends CompletionResolvedInfo {

  def patch(updateJobDescriptions: UpdateJobDescriptions): JobDescriptions = {
    this.copy(
      about = updateJobDescriptions.about.getOrElse(this.about),
      requirements = updateJobDescriptions.requirements.getOrElse(this.requirements),
      techStack = updateJobDescriptions.techStack.getOrElse(this.techStack),
    )
  }

}