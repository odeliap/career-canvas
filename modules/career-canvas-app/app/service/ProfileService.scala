package service

import careercanvas.io.{ResumeDao, UserDao}
import careercanvas.io.converter.Converters
import careercanvas.io.model.user.{Resume, UpdateUserInfo, UserInfo}
import careercanvas.io.storage.StorageService
import careercanvas.io.util.AwaitResult
import model.forms.EditProfileForm
import play.api.libs.Files
import play.api.mvc.MultipartFormData
import utils.StringUtils

import java.nio.file.{Paths, Files => NioFiles}
import java.sql.Timestamp
import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.ExecutionContext

@javax.inject.Singleton
class ProfileService @Inject()(
  userDao: UserDao,
  resumeDao: ResumeDao,
  storageService: StorageService,
  stringUtils: StringUtils
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
      linkedIn = editProfileForm.linkedIn,
      gitHub = editProfileForm.gitHub,
      website = editProfileForm.website
    )
    userDao.update(updateUserInfo).waitForResult
  }

  def getResumes(userId: String): Seq[Resume] = {
    resumeDao.getAll(userId.toLong).waitForResult
  }

  def addResume(userId: String, resume: MultipartFormData.FilePart[Files.TemporaryFile]): Unit = {
    val resumeUploadName = resolveResumeUploadName(resume)
    val (bucket, prefix) = uploadResume(userId, resume, resumeUploadName)
    registerResume(userId, resumeUploadName, bucket, prefix)
  }

  def deleteResume(userId: String, version: Int): Unit = {
    val resume = resumeDao.getByVersion(userId.toLong, version).waitForResult
    resumeDao.delete(userId.toLong, version)
    storageService.deleteFile(resume.bucket, resume.prefix)
  }

  def getPresignedUrl(userId: String, version: Int): String = {
    val resume = resumeDao.getByVersion(userId.toLong, version).waitForResult
    storageService.generateSignedUrl(resume.bucket, resume.prefix)
  }

  private def resolveResumeUploadName(resume: MultipartFormData.FilePart[Files.TemporaryFile]): String = {
    val originalFileName = Paths.get(resume.filename).getFileName.toString
    val (name, extension) = originalFileName.lastIndexOf('.') match {
      case -1 => (originalFileName, "")
      case i  => (originalFileName.substring(0, i), originalFileName.substring(i))
    }

    val sanitizedBaseName = stringUtils.sanitizeName(name)

    s"$sanitizedBaseName$extension"
  }

  private def uploadResume(userId: String, resume: MultipartFormData.FilePart[Files.TemporaryFile], resumeUploadName: String): (String, String) = {
    val tempFile = resume.ref.copyTo(Paths.get(resumeUploadName), replace = true)
    val bucket = "career-canvas-resumes"
    val key = s"$userId/$resumeUploadName"
    storageService.uploadFile(bucket, key, tempFile.toFile)
    NioFiles.deleteIfExists(tempFile)
    (bucket, key)
  }

  private def registerResume(userId: String, resumeUploadName: String, bucket: String, prefix: String): Unit = {
    val latestResumeIdOpt = resumeDao.getLatest(userId.toLong).waitForResult.map(_.version)
    val nextResumeId = if (latestResumeIdOpt.isDefined) latestResumeIdOpt.get + 1 else 1
    val resumeInfo = Resume(userId.toLong, nextResumeId, resumeUploadName, bucket, prefix, Timestamp.valueOf(LocalDateTime.now()))
    resumeDao.register(resumeInfo).waitForResult
  }

}
