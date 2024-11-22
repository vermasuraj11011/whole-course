package com.management.common.request
import play.api.libs.json.{Json, Reads}

case class Department2Req(name: String, organizationId: Int, isActive: Option[Boolean], head: Option[Int] = None)
  extends BaseRequest

object Department2Req extends BaseRequestCompanionObject[Department2Req] {
  override implicit val reads: Reads[Department2Req] = Json.reads[Department2Req]
}
