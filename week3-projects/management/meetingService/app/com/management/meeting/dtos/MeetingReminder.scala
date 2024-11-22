package com.management.meeting.dtos

import play.api.libs.json.{OFormat, Json}

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

object MeetingReminder {
  implicit val meetingReminderFormat: OFormat[MeetingReminder] = Json.format[MeetingReminder]
}
