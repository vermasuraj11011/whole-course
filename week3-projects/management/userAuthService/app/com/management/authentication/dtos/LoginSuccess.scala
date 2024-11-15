package com.management.authentication.dtos

import play.api.libs.json.{Format, Json}

case class LoginSuccess(token: String, email: String, name: String, role: String)

object LoginSuccess {
  implicit val format: Format[LoginSuccess] = Json.format[LoginSuccess]
}