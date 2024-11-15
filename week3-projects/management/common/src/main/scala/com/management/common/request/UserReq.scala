package com.management.common.request

case class UserReq(
  email: String,
  password: String,
  name: String,
  departmentId: Int,
  organizationId: Int,
  isActive: Boolean,
  token: String
)

object UserReq {
  import play.api.libs.json.{Format, Json}
  implicit val format: Format[UserReq] = Json.format[UserReq]
}
