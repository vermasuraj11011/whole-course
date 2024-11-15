package com.management.notification

import play.api.libs.json.{Format, Json}

case class EquipmentReturnReminder(
  equipmentId: Int,
  equipmentName: String,
  userId: Int,
  allocatedDate: Long,
  returnDate: Long,
  email: String,
  condition: String
)

object EquipmentReturnReminder {
  implicit val equipmentReturnReminderFormat: Format[EquipmentReturnReminder] = Json.format[EquipmentReturnReminder]
}
