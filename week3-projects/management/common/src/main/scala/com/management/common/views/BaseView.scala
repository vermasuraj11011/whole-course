package com.management.common.views

import play.api.libs.json.Writes

abstract class BaseView

abstract class BaseViewCompanionObject[V <: BaseView] {
  implicit val writes: Writes[V]
}
