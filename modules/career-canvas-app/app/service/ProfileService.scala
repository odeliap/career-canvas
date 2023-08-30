package service

import careercanvas.io.{ResumeDao, UserDao}
import careercanvas.io.converter.Converters
import careercanvas.io.model.user.{Resume, UpdateUserInfo, UserInfo}
import careercanvas.io.storage.StorageService
import careercanvas.io.util.AwaitResult
import model.forms.EditProfileForm
import play.api.libs.Files
import play.api.mvc.MultipartFormData

import java.nio.file.Paths
import java.sql.Timestamp
import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.ExecutionContext

@javax.inject.Singleton
class ProfileService @Inject()(
  userDao: UserDao,
  resumeDao: ResumeDao,
  storageService: StorageService
)(implicit ec: ExecutionContext)
  extends AwaitResult
    with Converters {

  def getUserInfo(id: String): UserInfo = {
    userDao.getUser(id.toLong).waitForResult.getOrElse(throw new IllegalArgumentException(s"No user exists with user id $id"))
  }

  def updateUserProfile(userId: String, editProfileForm: EditProfileForm): Unit = {
    val updateUserInfo = UpdateUserInfo(
      id = userId.toLong,
      email = editProfileForm.email,
      fullName = editProfileForm.name,
      resumeId = editProfileForm.resumeId,
      linkedIn = editProfileForm.linkedIn,
      gitHub = editProfileForm.gitHub,
      website = editProfileForm.website
    )
    userDao.update(updateUserInfo).waitForResult
  }

  def addResume(userId: String, resume: MultipartFormData.FilePart[Files.TemporaryFile]): Unit = {
    val resumeUploadName = resolveResumeUploadName(resume)
    val s3FullyQualifiedUploadPath = uploadResume(userId, resume, resumeUploadName)
    val resumeId = registerResume(userId, resumeUploadName, s3FullyQualifiedUploadPath)
    updateProfileResume(userId, resumeId)
  }

  private def resolveResumeUploadName(resume: MultipartFormData.FilePart[Files.TemporaryFile]): String = {
    val originalFileName = Paths.get(resume.filename).getFileName.toString
    val (name, extension) = originalFileName.lastIndexOf('.') match {
      case -1 => (originalFileName, "")
      case i  => (originalFileName.substring(0, i), originalFileName.substring(i))
    }

    val sanitizedBaseName = name
      .filter(c => c.isLetterOrDigit || c == '_')
      .toLowerCase
      .replace('_', '-')

    s"$sanitizedBaseName$extension"
  }

  private def uploadResume(userId: String, resume: MultipartFormData.FilePart[Files.TemporaryFile], resumeUploadName: String): String = {
    val tempFile = resume.ref.copyTo(Paths.get(resumeUploadName), replace = true)
    val bucket = "career-canvas-resumes"
    val key = s"$userId/$resumeUploadName"
    storageService.uploadFile(bucket, key, tempFile.toFile)
    s"$bucket/$key"
  }

  private def registerResume(userId: String, resumeUploadName: String, s3FullyQualifiedUploadPath: String): Long = {
    val resumeInfo = Resume(userId.toLong, 0L, resumeUploadName, s3FullyQualifiedUploadPath, Timestamp.valueOf(LocalDateTime.now()))
    resumeDao.register(resumeInfo).waitForResult
  }

  private def updateProfileResume(userId: String, resumeId: Long): Unit = {
    val updateUserInfo = UpdateUserInfo(id = userId.toLong, resumeId = Option(resumeId))
    userDao.update(updateUserInfo)
  }

}
