package com.management.common.request

import play.api.libs.json.{Json, Reads}

case class User2Req(name: String, email: String, password: String) extends BaseRequest

object User2Req extends BaseRequestCompanionObject[User2Req] {
  implicit val reads: Reads[User2Req] = Json.reads[User2Req]
}
