package com.management.common.request
import play.api.libs.json.{Json, Reads}

case class HeadUser2Req(
  email: String,
  password: String,
  name: String,
  role: String,
  departmentId: Int,
  organizationId: Int,
  isActive: Boolean,
  token: String
) extends BaseRequest

object HeadUser2Req extends BaseRequestCompanionObject[HeadUser2Req] {
  override implicit val reads: Reads[HeadUser2Req] = Json.reads[HeadUser2Req]
}
