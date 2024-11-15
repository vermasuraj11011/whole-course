package com.management.equipment.dtos

import play.api.libs.json.{Format, Json}

case class EquipmentAllocationReq(
  equipmentId: Int,
  allocatedQuantity: Int,
  allocatedDate: Long,
  isActive: Boolean,
  deallocatedDate: Option[Long],
  purpose: String,
  returnCondition: Option[String]
)

object EquipmentAllocationReq {
  implicit val equipmentAllocationReqFormat: Format[EquipmentAllocationReq] = Json.format[EquipmentAllocationReq]
}
