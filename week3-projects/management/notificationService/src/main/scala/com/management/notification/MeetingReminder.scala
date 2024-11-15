package com.management.notification

import play.api.libs.json.{Json, OFormat}

case class MeetingReminder(
  meetingId: Int,
  name: String,
  userId: Int,
  startTime: Long,
  isReminderSent: Boolean,
  email: String
)

object MeetingReminder {
  implicit val meetingReminderFormat: OFormat[MeetingReminder] = Json.format[MeetingReminder]
}
