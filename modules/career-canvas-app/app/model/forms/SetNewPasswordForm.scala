package model.forms

case class SetNewPasswordForm(
  newPassword: String,
  confirmNewPassword: String
)
