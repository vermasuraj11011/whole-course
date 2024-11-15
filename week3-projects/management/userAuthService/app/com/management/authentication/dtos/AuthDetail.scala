package com.management.authentication.dtos

import play.api.libs.json.{Format, Json}

case class AuthDetail(email: String, password: String)

object AuthDetail {
  implicit val format: Format[AuthDetail] = Json.format[AuthDetail]
}

// Example usage
//val jsonString = """{"email": "user@example.com", "password": "secret"}"""
//val json = Json.parse(jsonString)
//val authDetail = json.as[AuthDetail]

//println(authDetail) // Output: AuthDetail(user@example.com, secret)
