package com.management.equipment.dtos

case class EquipmentDetailReq(
  name: String,
  description: String,
  isActive: Boolean,
  totalQuantity: Int,
  availableQuantity: Int
)

import play.api.libs.json.{Format, Json}

object EquipmentDetailReq {
  implicit val equipmentDetailReqFormat: Format[EquipmentDetailReq] = Json.format[EquipmentDetailReq]
}
