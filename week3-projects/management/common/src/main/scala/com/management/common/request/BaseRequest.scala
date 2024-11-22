package com.management.common.request

import play.api.libs.json.Reads

abstract class BaseRequest

trait BaseRequestCompanionObject[T <: BaseRequest] {
  implicit val reads: Reads[T]
}
