package com.management.common.request

import play.api.libs.json.{Json, Reads}

case class Organization2Req(
  name: String,
  head_id: Option[Int] = None,
  address: String,
  phone: String,
  website: Option[String],
  isActive: Boolean = true,
  isMeetingServiceEnabled: Option[Boolean] = Some(false),
  isEquipmentServiceEnabled: Option[Boolean] = Some(false)
) extends BaseRequest

object OrganizationCreateReq extends BaseRequestCompanionObject[Organization2Req] {
  implicit val reads: Reads[Organization2Req] = Json.reads[Organization2Req]
}
