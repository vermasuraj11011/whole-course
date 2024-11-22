package com.management.common.views

import play.api.libs.json.{JsNull, Json, Writes}

case class ResponseData[T <: BaseView](data: T, message: Option[String] = None, status: Int, success: Boolean)

object ResponseData {
  implicit def writes[T <: BaseView](implicit writes: Writes[T]): Writes[ResponseData[T]] = Json.writes[ResponseData[T]]
}

case class ResponseDataList[T <: BaseView](data: List[T], message: Option[String] = None, status: Int, success: Boolean)

object ResponseDataList {
  implicit def writes[T <: BaseView](implicit writes: Writes[T]): Writes[ResponseDataList[T]] =
    Json.writes[ResponseDataList[T]]
}
