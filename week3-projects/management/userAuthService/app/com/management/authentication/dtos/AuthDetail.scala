package com.management.authentication.dtos

import play.api.libs.json.{Format, Json}

case class AuthDetail(email: String, password: String)

object AuthDetail {
  implicit val format: Format[AuthDetail] = Json.format[AuthDetail]
}