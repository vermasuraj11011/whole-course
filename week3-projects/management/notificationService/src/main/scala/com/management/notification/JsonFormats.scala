package com.management.notification

import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class EquipmentReminder(
  messageType: String = "equipment",
  equipmentId: Int,
  equipmentName: String,
  userId: Int,
  allocatedDate: Long,
  returnDate: Long,
  email: String,
  condition: String
)

case class MeetingReminder(
  messageType: String = "meeting",
  meetingId: Int,
  name: String,
  userId: Int,
  startTime: Long,
  endTime: Long,
  isReminderSent: Boolean,
  email: String
)

object JsonFormats extends DefaultJsonProtocol {
  implicit val equipmentReminderFormat: RootJsonFormat[EquipmentReminder] = jsonFormat8(EquipmentReminder)
  implicit val meetingReminderFormat: RootJsonFormat[MeetingReminder]     = jsonFormat8(MeetingReminder)
}
