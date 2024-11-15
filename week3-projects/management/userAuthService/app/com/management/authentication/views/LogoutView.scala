package com.management.authentication.views

case class LogoutView(message: String, name: String, email: String, organizationId: Int)

object LogoutView {

  import play.api.libs.json.{Format, Json}
  implicit val format: Format[LogoutView] = Json.format[LogoutView]
}
