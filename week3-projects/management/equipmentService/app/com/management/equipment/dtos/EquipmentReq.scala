package com.management.equipment.dtos

case class EquipmentReq(serialNo: String, equipmentDetailId: Int, status: String)

import play.api.libs.json.{Format, Json}

object EquipmentReq {
  implicit val equipmentReqFormat: Format[EquipmentReq] = Json.format[EquipmentReq]
}
